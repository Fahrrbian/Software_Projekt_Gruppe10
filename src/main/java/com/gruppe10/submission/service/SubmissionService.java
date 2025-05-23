package com.gruppe10.submission.service;

import com.gruppe10.submission.domain.Submission;
import com.gruppe10.usermanagement.domain.User;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

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

    public SubmissionService(SubmissionRepo submissionRepository) {
        this.submissionRepository = submissionRepository;
    }

    public Submission bewerten(Exam exam, User user, Map<String, Double> punkteMap) {
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

        return submissionRepository.save(sub);
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
}
