package com.gruppe10.examManagement.examAppointment.domain;

import com.gruppe10.usermanagement.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentExamRepository extends JpaRepository<StudentExam, Long> {

    List<StudentExam> findByExamId(Long examId);

    Optional<StudentExam> findByStudent_IdAndExam_Id(Long studentId, Long examId);

    @Query("SELECT se FROM StudentExam se WHERE se.student = :student AND se.gesperrt = false")
    Page<StudentExam> findByStudentAndGesperrtFalse(@Param("student") Optional<User> student, Pageable pageable);

    @Query("SELECT se FROM StudentExam se WHERE se.student = :student AND se.completed = true")
    List<StudentExam> findCompletedExamsByStudent(@Param("student") User student);

    Optional<StudentExam> findBySubmission_Id(Long submissionId);
}