package com.gruppe10.submission.service;

import com.gruppe10.examManagement.examAppointment.domain.StudentExam;
import com.gruppe10.examManagement.examAppointment.domain.StudentExamRepository;
import com.gruppe10.submission.DTOs.SubmissionDto;
import com.gruppe10.submission.domain.Submission;
import com.gruppe10.submission.domain.SubmissionAnswer;
import com.gruppe10.submission.domain.SubmissionStatus;
import com.gruppe10.usermanagement.domain.User;
import com.vaadin.flow.router.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.gruppe10.examManagement.exam.domain.Exam;
import com.gruppe10.usermanagement.domain.Student;
import com.gruppe10.submission.repo.SubmissionRepo;
import org.springframework.transaction.annotation.Transactional;

/**
 * SubmissionService.java
 * <p>
 * Created by Fabian Holtapel on 14.05.2025.
 * <p>
 * Description:
 * TODO: Beschreibung einfügen.
 */
@Service
public class SubmissionService {
    private final SubmissionRepo submissionRepository;
    private final StudentExamRepository studentExamRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public SubmissionService(SubmissionRepo submissionRepository, StudentExamRepository studentExamRepository, ApplicationEventPublisher eventPublisher) {
        this.submissionRepository = submissionRepository;
        this.studentExamRepository = studentExamRepository;
        this.eventPublisher = eventPublisher;
    }

    public SubmissionService(SubmissionRepo submissionRepository, StudentExamRepository studentExamRepository) {
        this.submissionRepository = submissionRepository;
        this.studentExamRepository = studentExamRepository;
        this.eventPublisher = new ApplicationEventPublisher() {
            @Override
            public void publishEvent(Object event) {

            }
        };
    }

    @Transactional
    public Submission bewerten(Exam exam, User user, Map<String, Double> punkteMap, Map<String, String> rawAnswers) {
        double summe = punkteMap.values().stream().mapToDouble(Double::doubleValue).sum();
        boolean bestanden = summe >= exam.getBestehensgrenze();

        Submission sub = new Submission();
        sub.setExam(exam);

        if (user instanceof Student) {
            sub.setStudent((Student) user);
        } else {
            throw new IllegalArgumentException("User is not a Student");
        }

        sub.setAufgabenErgebnisse(punkteMap);
        sub.setTotalPoints(summe);
        sub.setPassed(bestanden);

        if (exam.isHasFreeTextQuestions()) {
            sub.setStatus(SubmissionStatus.PENDING_REVIEW);
        } else {
            sub.setStatus(SubmissionStatus.AUTO_GRADED);
        }

        rawAnswers.forEach((questionId, answerData) -> {
            SubmissionAnswer sa = new SubmissionAnswer();
            sa.setQuestionId(questionId);
            sa.setAnswerData(answerData);
            sa.setSubmission(sub);
            sub.getAnswers().add(sa);
        });

        Submission saved = submissionRepository.save(sub);
        eventPublisher.publishEvent(new SubmissionSubmittedEvent(this, saved));
        return saved;
    }
    public List<Submission> getSubmissionsByExam(Exam exam) {

        return submissionRepository.findByExam(exam);
    }

    public List<Submission> getSubmissionsByStudent(Student student) {
        return submissionRepository.findByStudent(student);
    }
    @Transactional(readOnly = true)
    public List<Submission> getSubmissionsByStudentFullyFetched(Student student) {
        return submissionRepository.findByStudentWithAufgabenErgebnisseEager(student);
    }

    public int countParticipants(Exam exam) {

        return submissionRepository.countByExam(exam);
    }

    public int countPassed(Exam exam) {

        return submissionRepository.countByExamAndPassedTrue(exam);
    }

    @Transactional(readOnly = true)
    public List<StudentExam> getCompletedExamsByStudent(Student student) {
        return studentExamRepository.findCompletedExamsByStudent(student);
    }
    
    @Transactional(readOnly = true)
    public Optional<Submission> getSubmissionByStudentAndExam(Student student, Exam exam) {
        return submissionRepository.findByStudentAndExam(student, exam);
    }
    public Submission save(Submission submission) {
        return submissionRepository.save(submission);
    }
    public Optional<Submission> findById(Long id) {
        return submissionRepository.findById(id);
    }

    public boolean existsByExam(Exam exam) {
        return submissionRepository.existsByExam(exam);
    }

    @Transactional
    public void updateSubmissionFromDto(SubmissionDto dto) {
        Submission submission = submissionRepository.findById(dto.getSubmissionId())
                .orElseThrow(() -> new NotFoundException("Submission nicht gefunden"));

        //Vergabe von neuen Punkten
        Map<String, Double> neuePunkte = dto.getPerQuestionPoints();
        submission.setAufgabenErgebnisse(new HashMap<>(neuePunkte));

        //Berechnung der Gesamtpunktzahl
        double totalPoints = submission.calculateTotal();
        submission.setTotalPoints(totalPoints);

        //Überprüfung, ob bestanden oder nicht
        double maxPoints = submission.getExam().getExercises().stream()
                .mapToDouble(e -> e.getScore() != null ? e.getScore() : 0.0)
                .sum();

        boolean passed = maxPoints > 0 && totalPoints >= (submission.getExam().getBestehensgrenze());
        submission.setPassed(passed);

        submission.setStatus(SubmissionStatus.REVIEWED);

        submissionRepository.save(submission);

        studentExamRepository.findBySubmission_Id(submission.getId())
                .ifPresent(studentExam -> {
                    studentExam.setCompleted(true);
                    studentExamRepository.save(studentExam);
                });
    }

    @Transactional
    public Submission findFullyLoaded(Long id) {
        Submission submission = submissionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Nicht gefunden"));

        //Zugriff erzwingt Laden der Ergebnisse
        submission.getAufgabenErgebnisse().size();
        submission.getAnswers().size();

        return submission;
    }

}