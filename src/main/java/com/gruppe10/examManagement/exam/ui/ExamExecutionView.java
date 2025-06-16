/**
 * Author: Christian Markow
 * Date: 27.05.2025
 */

package com.gruppe10.examManagement.exam.ui;

import com.gruppe10.base.ui.Layout.MainLayout;
import com.gruppe10.examManagement.exam.domain.Exam;
import com.gruppe10.examManagement.exam.domain.ExamRepository;
import com.gruppe10.examManagement.exam.service.ExamService;
import com.gruppe10.examManagement.examAppointment.domain.StudentExam;
import com.gruppe10.examManagement.examAppointment.domain.StudentExamRepository;
import com.gruppe10.exercisemanagement.domain.*;
import com.gruppe10.exercisemanagement.domain.Answer;
import com.gruppe10.exercisemanagement.service.ExerciseService;
import com.gruppe10.submission.domain.*;
import com.gruppe10.submission.service.EvaluationService;
import com.gruppe10.submission.service.SubmissionService;
import com.gruppe10.timer.Timer;
import com.gruppe10.usermanagement.domain.Student;
import com.gruppe10.usermanagement.domain.User;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.*;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.gruppe10.base.ui.security.SecurityUtils.getCurrentUser;

@Route(value = "exam/:examId", layout = MainLayout.class)
@PageTitle("Prüfung")
@RolesAllowed({"INSTRUCTOR", "STUDENT"})
public class ExamExecutionView extends VerticalLayout implements BeforeEnterObserver {

    private final ExerciseService exerciseService;
    private final ExamService examService;
    private final SubmissionService submissionService;
    private final ExamRepository examRepository;
    private final StudentExamRepository studentExamRepository;
    private final EvaluationService evaluationService;
    private StudentExam studentExam;

    private List<Exercise> exercises = new ArrayList<>();
    private Map<Long, Answer> userAnswers = new HashMap<>();
    private final Map<Long, Map<String, ComboBox<String>>> assignmentComboBoxMap = new HashMap<>();

    private int currentIndex = 0;
    private Component currentComponent;
    private Timer timer;

    @Autowired
    public ExamExecutionView(ExerciseService exerciseService, ExamService examService, SubmissionService submissionService, ExamRepository examRepository, StudentExamRepository studentExamRepository, EvaluationService evaluationService) {
        this.exerciseService = exerciseService;
        this.examService = examService;
        this.submissionService = submissionService;
        this.examRepository = examRepository;
        this.studentExamRepository = studentExamRepository;
        this.evaluationService = evaluationService;
        setSpacing(true);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        String examIdParam = event.getRouteParameters().get("examId").orElse(null);
        if (examIdParam != null) {
            try {
                long examId = Long.parseLong(examIdParam);
                loadExam(examId);
            } catch (NumberFormatException e) {
                showError("Ungültige Prüfung-ID.");
            }
        } else {
            showError("Fehler: Prüfung nicht gefunden.");
        }
    }

    private void loadExam(Long examId) {
        Optional<User> currentUser = getCurrentUser();
        if (currentUser.isEmpty()) {
            showError("Benutzer nicht gefunden.");
            return;
        }

        Student student = (Student) currentUser.get();

        //Logging
//        System.out.println("Student-ID: " + student.getId());
//        System.out.println("Exam-ID: " + examId);

        int studentNumber = student.getStudentNumber();
        String studentNrString = String.valueOf(studentNumber);

        Optional<StudentExam> optionalExam = studentExamRepository.findByMatrikelnummerAndExam_IdAndGesperrtFalse(studentNrString, examId);
        if (optionalExam.isEmpty()) {
            //Prüfung wurde noch nicht gestartet. Neue nutzerspezifische Prüfung wird erzeugt.
            Optional<Exam> examOpt = examRepository.findById(examId);
            if (examOpt.isEmpty()) {
                showError("Prüfung nicht vorhanden.");
                return;
            }

            Exam exam = examOpt.get();

            StudentExam newStudentExam = new StudentExam();
            newStudentExam.setExam(exam);
            newStudentExam.setStudent(student);
            newStudentExam.setVorname(student.getForename());
            newStudentExam.setNachname(student.getSurname());
            newStudentExam.setStartTime(LocalDateTime.now());
            newStudentExam.setGesperrt(false);
            newStudentExam.setCompleted(false);

            studentExam = studentExamRepository.save(newStudentExam);
        } else {
            studentExam = optionalExam.get();

//            if (studentExam.isGesperrt()) {
//                showError("Diese Prüfung ist gesperrt und kann nicht mehr bearbeitet werden.");
//                return;
//            }
        }

        exercises = exerciseService.getAllByExamId(examId);

        //30 Minuten-Timer
        timer = new Timer(30 * 60 * 1000L, this::onTimeUp);
        add(timer);

        showExercise(currentIndex);
    }

    private void showExercise(int index) {
        removeAll();
        add(timer);

        if (index >= exercises.size()) {
            showSummary();
            return;
        }

        Exercise exercise = exercises.get(index);
        add(new H4("Frage " + (index + 1) + ": " + exercise.getExerciseText()));
        add(new H5("Mögliche Punktzahl: " + exercise.getScore()));

        currentComponent = createInputComponent(exercise);
        add(currentComponent);

        HorizontalLayout navButtons = getHorizontalLayout(index);
        add(navButtons);
    }

    //Erzeugung der Navigationselemente (Buttons und Dropdown)
    private HorizontalLayout getHorizontalLayout(int index) {
        HorizontalLayout navButtons = new HorizontalLayout();

        Button prevButton = new Button("Zurück", e -> {
            saveAnswer();
            if (currentIndex > 0) {
                currentIndex--;
                showExercise(currentIndex);
            }
        });
        prevButton.setEnabled(index > 0);

        Button nextButton = new Button(index == exercises.size() - 1 ? "Zur Zusammenfassung" : "Weiter", e -> {
            saveAnswer();
            currentIndex++;
            showExercise(currentIndex);
        });

        Select<Integer> jumpToSelect = new Select<>();
        jumpToSelect.setItems(IntStream.range(0, exercises.size()).boxed().toList());
        jumpToSelect.setValue(index);
        jumpToSelect.setItemLabelGenerator(i -> "Aufgabe " + (i + 1));
        jumpToSelect.addValueChangeListener(e -> {
            saveAnswer();
            currentIndex = e.getValue();
            showExercise(currentIndex);
        });

        navButtons.add(prevButton, nextButton, jumpToSelect);
        return navButtons;
    }

    //Erzeugung der Aufgabenansicht (je nach Aufgabentyp)
    private Component createInputComponent(Exercise exercise) {
        Answer saved = userAnswers.get(exercise.getId());

        if (exercise instanceof SingleChoice singleChoice) {
            RadioButtonGroup<String> radio = new RadioButtonGroup<>();
            radio.setItems(singleChoice.getChoiceOptions().stream().map(ChoiceOption::getText).toList());
            if (saved != null && saved.getSelectedOptions() != null && !saved.getSelectedOptions().isEmpty()) {
                radio.setValue(saved.getSelectedOptions().get(0));
            }
            return radio;

        } else if (exercise instanceof MultipleChoice multipleChoice) {
            CheckboxGroup<String> checkbox = new CheckboxGroup<>();
            checkbox.setItems(multipleChoice.getChoiceOptions().stream().map(ChoiceOption::getText).toList());
            if (saved != null && saved.getSelectedOptions() != null) {
                checkbox.setValue(new HashSet<>(saved.getSelectedOptions()));
            }
            return checkbox;

        } else if (exercise instanceof FreetextExercise freetextExercise) {
            TextArea textArea = new TextArea();
            textArea.setWidthFull();
            if (saved != null && saved.getTextAnswer() != null) {
                textArea.setValue(saved.getTextAnswer());
            }
            return textArea;

        } else if (exercise instanceof AssignmentExercise assignmentExercise) {
            Map<String, ComboBox<String>> comboBoxes = new HashMap<>();
            VerticalLayout layout = new VerticalLayout();

            List<String> options = assignmentExercise.getAssignmentPairs().stream()
                    .map(AssignmentPair::getPartTwo)
                    .collect(Collectors.toList());

            Collections.shuffle(options);

            for (AssignmentPair pair : assignmentExercise.getAssignmentPairs()) {
                String left = pair.getPartOne();
                ComboBox<String> comboBox = new ComboBox<>();
                comboBox.setLabel(left);
                comboBox.setItems(options);
                comboBox.setWidth("40%");

                if (saved != null && saved.getAssignmentMappings() != null) {
                    comboBox.setValue(saved.getAssignmentMappings().get(left));
                }

                layout.add(comboBox);
                comboBoxes.put(left, comboBox);
            }

            assignmentComboBoxMap.put(exercise.getId(), comboBoxes);

            return layout;

        }

        return new Paragraph("Unbekannter Aufgabentyp.");
    }

    private void saveAnswer() {
        if (currentIndex >= exercises.size()) return;

        Exercise exercise = exercises.get(currentIndex);
        Answer answer = new Answer();
        answer.setExercise(exercise);

        if (currentComponent instanceof RadioButtonGroup<?>) {
            String selected = ((RadioButtonGroup<String>) currentComponent).getValue();
            answer.setSelectedOptions(selected != null ? List.of(selected) : List.of());
        } else if (currentComponent instanceof CheckboxGroup<?>) {
            Set<String> selected = ((CheckboxGroup<String>) currentComponent).getValue();
            answer.setSelectedOptions(selected != null ? new ArrayList<>(selected) : new ArrayList<>());
        } else if (currentComponent instanceof TextArea) {
            String text = ((TextArea) currentComponent).getValue();
            answer.setTextAnswer(text != null ? text : "");
        } else if (exercise instanceof AssignmentExercise) {
            Map<String, ComboBox<String>> comboBoxes = assignmentComboBoxMap.get(exercise.getId());
            if (comboBoxes != null) {
                Map<String, String> mappings = new HashMap<>();
                for (Map.Entry<String, ComboBox<String>> entry : comboBoxes.entrySet()) {
                    mappings.put(entry.getKey(), entry.getValue().getValue());
                }
                answer.setAssignmentMappings(mappings);
            }
        }

        //Logging
//        System.out.println("Speichere Antwort für Exercise " + exercise.getId() + ": " + answer.getSelectedOptions());

        userAnswers.put(exercise.getId(), answer);
    }

    private void showSummary() {
        removeAll();
        add(timer);
        add(new H3("Zusammenfassung"));

        for (Exercise exercise : exercises) {
            Answer answer = userAnswers.get(exercise.getId());
            add(new Paragraph("Frage: " + exercise.getExerciseText()));
            add(new Paragraph("Mögliche Punktzahl: " + exercise.getScore()));

            String preview = "(keine Antwort)";
            if (answer != null) {
                if (exercise instanceof SingleChoice) {
                    List<String> opts = answer.getSelectedOptions();
                    preview = opts != null && !opts.isEmpty() ? opts.get(0) : "(keine Antwort)";
                } else if (exercise instanceof MultipleChoice) {
                    List<String> opts = answer.getSelectedOptions();
                    preview = opts != null && !opts.isEmpty() ? String.join(", ", opts) : "(keine Antwort)";
                } else if (exercise instanceof FreetextExercise) {
                    String text = answer.getTextAnswer();
                    preview = text != null && !text.isBlank() ? text : "(keine Antwort)";
                } else if (exercise instanceof AssignmentExercise) {
                    Map<String, String> mappings = answer.getAssignmentMappings();
                    if (mappings != null && !mappings.isEmpty()) {
                        StringBuilder sb = new StringBuilder();
                        for (Map.Entry<String, String> entry : mappings.entrySet()) {
                            String key = entry.getKey();
                            String value = entry.getValue() != null ? entry.getValue() : "(keine Antwort)";
                            sb.append(key).append(" ➝ ").append(value).append(";\n");
                        }
                        preview = !sb.isEmpty() ? sb.toString() : "(keine Antwort)";
                    }
                }
            }

            add(new Paragraph("Antwort: " + preview));
            add(new Hr());
        }

        Button submitButton = new Button("Prüfung abschließen", e -> {
            ConfirmDialog dialog = new ConfirmDialog();
            dialog.setText("Möchten Sie die Prüfung wirklich abschließen?");

            dialog.setConfirmText("Ja");
            dialog.setCancelable(true);
            dialog.setCancelText("Nein");

            dialog.addConfirmListener(e2 -> submitExam());

            dialog.open();
        });

        Select<Integer> jumpToSelect = new Select<>();
        jumpToSelect.setItems(IntStream.range(0, exercises.size()).boxed().toList());
        jumpToSelect.setPlaceholder("Zusammenfassung");
        jumpToSelect.setItemLabelGenerator(i -> "Aufgabe " + (i + 1));
        jumpToSelect.addValueChangeListener(e -> {
            saveAnswer();
            currentIndex = e.getValue();
            showExercise(currentIndex);
        });

        HorizontalLayout buttons = new HorizontalLayout(submitButton, jumpToSelect);
        buttons.setSpacing(true);
        add(buttons);
    }

    private void onTimeUp() {
        Notification.show("Zeit abgelaufen. Prüfung wird abgegeben.");
        submitExam();
    }

    //Abgabe der Prüfung (als Submission) und Evaluierung der Antworten
    private void submitExam() {
        studentExam.setEndTime(LocalDateTime.now());

        Map<String, String> answers = new HashMap<>();
        for (Map.Entry<Long, Answer> entry : userAnswers.entrySet()) {
            Long exerciseId = entry.getKey();
            Answer userAnswer = entry.getValue();

            String answerData = "";

            if (userAnswer.getSelectedOptions() != null && !userAnswer.getSelectedOptions().isEmpty()) {
                answerData = String.join(";; ", userAnswer.getSelectedOptions());
            } else if (userAnswer.getTextAnswer() != null) {
                answerData = userAnswer.getTextAnswer();
            } else if (userAnswer.getAssignmentMappings() != null && !userAnswer.getAssignmentMappings().isEmpty()) {
                answerData = userAnswer.getAssignmentMappings().entrySet().stream()
                        .map(e -> e.getKey() + "=>" + e.getValue())
                        .collect(Collectors.joining(";; "));
            }

            answers.put(String.valueOf(exerciseId), answerData);
        }

        Map<String, com.gruppe10.submission.domain.Answer> domainAnswers = userAnswers.entrySet().stream()
                .map(entry -> {
                    Answer userAnswer = entry.getValue();

                    String questionId;
                    if (userAnswer.getExercise() != null && userAnswer.getExercise().getId() != null) {
                        questionId = userAnswer.getExercise().getId().toString();
                    } else {
                        questionId = String.valueOf(entry.getKey());
                    }

                    //Logging
//                    System.out.println("--- Verarbeitung der Antwort ---");
//                    System.out.println("Frage-ID: " + entry.getKey());
//                    System.out.println("selectedOptions: " + userAnswer.getSelectedOptions());
//                    System.out.println("textAnswer: " + userAnswer.getTextAnswer());
//                    System.out.println("assignmentMappings: " + userAnswer.getAssignmentMappings());
//                    System.out.println("----------------------------");

                    com.gruppe10.submission.domain.Answer answer = null;

                    if (userAnswer.getSelectedOptions() != null && !userAnswer.getSelectedOptions().isEmpty()) {
                        if (userAnswer.getSelectedOptions().size() == 1) {
                            var sc = new SingleChoiceAnswer();
                            sc.setQuestionId(questionId);
                            sc.setSelectedOptionId(userAnswer.getSelectedOptions().get(0));
                            answer = sc;
                        } else {
                            var mc = new MultipleChoiceAnswer();
                            mc.setQuestionId(questionId);
                            mc.setSelectedOptionIds(userAnswer.getSelectedOptions());
                            answer = mc;
                        }
                    } else if (userAnswer.getTextAnswer() != null && !userAnswer.getTextAnswer().isBlank()) {
                        var ft = new FreeTextAnswer();
                        ft.setQuestionId(questionId);
                        ft.setText(userAnswer.getTextAnswer());
                        answer = ft;
                    } else if (userAnswer.getAssignmentMappings() != null && !userAnswer.getAssignmentMappings().isEmpty()) {
                        var assign = new AssignmentAnswer();
                        assign.setQuestionId(questionId);
                        assign.setAssignmentMappings(userAnswer.getAssignmentMappings());
                        answer = assign;
                    }

                    return answer != null ? Map.entry(questionId, answer) : null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));


        var result = evaluationService.evaluateExam(studentExam.getExam(), domainAnswers);

        //Logging
//        System.out.println("--- Evaluation Result ---");
//        System.out.println("Per Question Points: " + result.getPerQuestionPoints());
//        System.out.println("Total Points: " + result.getTotalPoints());
//        System.out.println("Passed: " + result.isPassed());
//        System.out.println("-------------------------");

        //Logging
//        Map<String, Double> perQuestionPoints = result.getPerQuestionPoints();
//        System.out.println("Punkte vor Bewertung speichern: " + perQuestionPoints);

        Optional<User> currentUser = getCurrentUser();
        Student student = (Student) currentUser.get();

        Submission submission = submissionService.bewerten(studentExam.getExam(), student, result.getPerQuestionPoints(), answers);
        //Logging
//        System.out.println("--- Bewertung starten ---");
//        System.out.println("Exam: " + studentExam.getId());
//        System.out.println("Student: " + studentExam.getStudent().getId());
//        System.out.println("Punkte pro Frage: " + perQuestionPoints);
//        System.out.println("Antworten roh: " + answers);

        studentExam.setSubmission(submission);
        studentExam.setGesperrt(true);
        studentExam.getExam().setOpenToCorrect(true);

        studentExamRepository.save(studentExam);
//        examRepository.save(studentExam.getExam());

        Notification.show("Prüfung abgegeben.");
        UI.getCurrent().navigate("/");
    }

    private void showError(String message) {
        removeAll();
        add(new Paragraph(message));
    }

}