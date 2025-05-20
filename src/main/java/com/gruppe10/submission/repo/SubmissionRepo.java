package com.gruppe10.submission.repo;

import com.gruppe10.exam.domain.Exam;
import com.gruppe10.submission.domain.Submission;
import com.gruppe10.usermanagement.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * SubmissionRepo.java
 * <p>
 * Created by Fabian Holtapel on 14.05.2025.
 * <p>
 * Description:
 * TODO: Beschreibung einfügen.
 */

public interface SubmissionRepo extends JpaRepository<Submission, Long> {
    List<Submission> findByExam(Exam exam);
    List<Submission> findByStudent(Student student);
    int countByExam(Exam exam);
    int countByExamAndPassedTrue(Exam exam);
    @Query("SELECT s FROM Submission s LEFT JOIN FETCH s.aufgabenErgebnisse WHERE s.student = :student")
    List<Submission> findByStudentWithAufgabenErgebnisseEager(@Param("student") Student student);

}
