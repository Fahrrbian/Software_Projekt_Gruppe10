package com.gruppe10;
import com.gruppe10.examManagement.exam.domain.Exam;
import com.gruppe10.examManagement.exam.domain.ExamExercise;
import com.gruppe10.examManagement.exam.domain.ExamExerciseId;
import com.gruppe10.exercisemanagement.domain.FreetextExercise;
import com.gruppe10.exercisemanagement.domain.SingleChoice;
import com.gruppe10.exercisemanagement.domain.ChoiceOption;
import com.gruppe10.submission.domain.Answer;
import com.gruppe10.submission.domain.FreeTextAnswer;
import com.gruppe10.submission.domain.SingleChoiceAnswer;
import com.gruppe10.submission.domain.Submission;
import com.gruppe10.submission.service.SubmissionService;
import com.gruppe10.submission.service.EvaluationService;
import com.gruppe10.usermanagement.domain.Student;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
/**
 * LocalEvaluationTest.java
 * <p>
 * Created by Fabian Holtapel on 01.06.2025.
 * <p>
 * Description:
 * TODO: Beschreibung einfügen.
 */

public class LocalEvaluationTest {

    private EvaluationService evalService;
    private SubmissionService submissionService;

    @BeforeEach
    void setUp() {
        // Wir wollen den „reinen“ SubmissionService aufrufen, also mocken wir das Repository.
        var repoMock = Mockito.mock(com.gruppe10.submission.repo.SubmissionRepo.class);
        // Wenn submissionRepo.save(...) gerufen wird, returne einfach das Argument.
        Mockito.when(repoMock.save(Mockito.any(Submission.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        ApplicationEventPublisher eventPublisherMock = Mockito.mock(ApplicationEventPublisher.class);
        // Publisher brauchen wir nur, wenn du auf Events hörst. Hier reicht null.
        this.submissionService = new SubmissionService(repoMock, eventPublisherMock);
        this.evalService       = new EvaluationService();
    }

    @Test
    void testEinfachesExamManuellBewerten() throws Exception {
        // 1) Exam anlegen
        Exam exam = new Exam();
        exam.setId(99L);
        exam.setTitle("Beispiel-Prüfung");
        exam.setCreationDate(Instant.now());
        exam.setBestehensgrenze(3.0);      // ab 3 Punkten ist "bestanden"
        exam.setHasFreeTextQuestions(true); // wir haben hier mindestens 1 Freitext-Frage
        exam.setAutoPublishResults(false);  // Ergebnisse werden erst nach manueller Korrektur freigegeben

        // 2) Zwei Exercises hinzufügen: SingleChoice + FreeText
        // --- a) SingleChoice
        SingleChoice sc = new SingleChoice();
        sc.setId(10L);
        sc.setExerciseText("Was ist 2+2?");
        sc.setScore(1);
        // Wir benötigen mindestens 2 ChoiceOption-Objekte. Eine korrekt, eine falsch:
        ChoiceOption optA = new ChoiceOption();
        optA.setId(100L);
        optA.setText("3");
        optA.setCorrect(false);

        ChoiceOption optB = new ChoiceOption();
        optB.setId(101L);
        optB.setText("4");
        optB.setCorrect(true);

        sc.setChoiceOptions(Set.of(optA, optB));

        // --- b) FreeText
        FreetextExercise ft = new FreetextExercise();
        ft.setId(20L);
        ft.setExerciseText("Beschreibe kurz das Konzept von Vererbung in Java.");
        ft.setScore(5);

        // 3) Wir brauchen die Join-Entität ExamExercise, um Reihenfolge festzulegen:
        //    In Exam.getQuestions() wird über exam.getExamExercises() kommen.
        ExamExerciseId id1 = new ExamExerciseId(exam.getId(), sc.getId());
        ExamExercise ee1   = new ExamExercise();
        ee1.setId(id1);
        ee1.setExam(exam);
        ee1.setExercise(sc);
        ee1.setPosition(1);

        ExamExerciseId id2 = new ExamExerciseId(exam.getId(), ft.getId());
        ExamExercise ee2   = new ExamExercise();
        ee2.setId(id2);
        ee2.setExam(exam);
        ee2.setExercise(ft);
        ee2.setPosition(2);

        // Füge die Join-Entities zur Prüfung hinzu:
        exam.getExamExercises().addAll(Arrays.asList(ee1, ee2));

        // 4) Antworten‐Map zusammenbauen (student liefert nur Strings)
        //    Keys sind questionId als String, Values sind rawAnswer als String.

        // --- a) SingleChoice: der Student wählt OptionId = "101"
        String qidSC = sc.getId().toString();   // "10"
        String chosenOptionId = optB.getId().toString(); // "101"

        // --- b) FreeText: reiner Text, später manuell bewerten
        String qidFT = ft.getId().toString();   // "20"
        String freitextAntwort = "In Java erbt eine Unterklasse Methoden, Felder und Verträge von der Oberklasse.";

        Map<String,String> rawAnswers = new HashMap<>();
        rawAnswers.put(qidSC, chosenOptionId);
        rawAnswers.put(qidFT, freitextAntwort);

        // 5) Den DTO-ähnlichen Schritt: Raw → Domain-Answer-Objekte
        //    Wir imitieren hier, was ExamSubmissionDto.toDomainAnswers(...) macht:
        Map<String, Answer> domainAnswers = new HashMap<>();

        // a) SingleChoiceAnswer
        SingleChoiceAnswer scAntwort = new SingleChoiceAnswer();
        scAntwort.setQuestionId(qidSC);
        scAntwort.setSelectedOptionId(chosenOptionId);
        domainAnswers.put(qidSC, scAntwort);

        // b) FreeTextAnswer
        FreeTextAnswer ftAntwort = new FreeTextAnswer();
        ftAntwort.setQuestionId(qidFT);
        ftAntwort.setText(freitextAntwort);
        domainAnswers.put(qidFT, ftAntwort);

        // 6) Jetzt rufen wir evaluateExam auf (automatische Punktevergabe)
        var result = evalService.evaluateExam(exam, domainAnswers);
        // result.getPerQuestionPoints() sollte:  { "10": 1.0, "20": 0.0 }
        Assertions.assertThat(result.getPerQuestionPoints().get(qidSC)).isEqualTo(1.0);
        Assertions.assertThat(result.getPerQuestionPoints().get(qidFT)).isEqualTo(0.0);

        // Gesamtpunkte:
        Assertions.assertThat(result.getTotalPoints()).isEqualTo(1.0);
        // Bestehensgrenze war 3.0 → bestanden=false
        Assertions.assertThat(result.isPassed()).isFalse();

        // 7) Nun legen wir eine Dummy-Student-Instanz an
        Student dummyStudent = new Student();
        dummyStudent.setStudentNumber(555);
        dummyStudent.setEmail("max.mustermann@student.de");

        // 8) Rufe submissionService.bewerten(...) auf und speichere es
        Submission saved = submissionService.bewerten(
                exam,
                dummyStudent,
                result.getPerQuestionPoints(),  // Map<String,Double>
                rawAnswers                       // Map<String,String>
        );

        // Prüfen, dass die Submission die erwarteten Werte enthält:
        Assertions.assertThat(saved.getExam().getId()).isEqualTo(99L);
        Assertions.assertThat(saved.getStudent().getEmail()).isEqualTo("max.mustermann@student.de");
        Assertions.assertThat(saved.getTotalPoints()).isEqualTo(1.0);
        Assertions.assertThat(saved.getPassed()).isFalse();
        // Da FreeText enthalten ist, sollte status = PENDING_REVIEW sein
        Assertions.assertThat(saved.getStatus()).isEqualTo(com.gruppe10.submission.domain.SubmissionStatus.PENDING_REVIEW);

        // Außerdem sollten im saved.getAnswers() zwei Einträge sein:
        var answersList = saved.getAnswers();
        Assertions.assertThat(answersList).hasSize(2);
        // Und in saved.getAufgabenErgebnisse() sollten genau 2 Keys („10“ und „20“) sein:
        Assertions.assertThat(saved.getAufgabenErgebnisse()).containsKeys(qidSC, qidFT);
        // Für die FT-Frage haben wir bislang 0.0 Punkte gesetzt
        Assertions.assertThat(saved.getAufgabenErgebnisse().get(qidFT)).isEqualTo(0.0);
    }
}
