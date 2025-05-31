package com.gruppe10.submission.service;

import com.gruppe10.submission.domain.Submission;
import com.gruppe10.submission.domain.SubmissionAnswer;
import com.gruppe10.submission.domain.SubmissionStatus;
import com.gruppe10.usermanagement.domain.User;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
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
    private final ApplicationEventPublisher eventPublisher;


    public SubmissionService(SubmissionRepo submissionRepository, ApplicationEventPublisher eventPublisher) {
        this.submissionRepository = submissionRepository;
        this.eventPublisher = eventPublisher;
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
    public Optional<Submission> getSubmissionByStudentAndExam(Student student, Exam exam) {
        return submissionRepository.findByStudentAndExam(student, exam);
    }
    public Submission save(Submission submission) {
        return submissionRepository.save(submission);
    }
    public Optional<Submission> findById(Long id) {
        return submissionRepository.findById(id);
    }
}
