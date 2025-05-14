package com.gruppe10.submission.service;

import com.gruppe10.submission.domain.Submission;
import org.springframework.stereotype.Service;
import java.util.List;
import com.gruppe10.exam.domain.Exam;
import com.gruppe10.usermanagement.domain.Student+
import com.gruppe10.submission.repo.SubmissionRepo;
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

    public List<Submission> getSubmissionsByExam(Exam exam) {
        return submissionRepository.findByExam(exam);
    }

    public List<Submission> getSubmissionsByStudent(Student student) {
        return submissionRepository.findByStudent(student);
    }

    public int countParticipants(Exam exam) {
        return submissionRepository.countByExam(exam);
    }

    public int countPassed(Exam exam) {
        return submissionRepository.countByExamAndPassedTrue(exam);
    }
}
