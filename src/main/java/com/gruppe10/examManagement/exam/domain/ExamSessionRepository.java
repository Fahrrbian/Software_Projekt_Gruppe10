///**
// * Author: Christian Markow
// * Date: 27.05.2025
// */
//
//package com.gruppe10.examManagement.exam.domain;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//
//import java.util.Optional;
//
//public interface ExamSessionRepository extends JpaRepository<ExamSession, Long> {
//
//    Optional<ExamSession> findByExamIdAndUserId(Long examId, Long userId);
//    Optional<ExamSession> findByExamId(Long examId);
//    Optional<ExamSession> findByAppointmentIdAndUserId(Long appointmentId, Long userId);
//    @Query("SELECT s FROM ExamSession s WHERE s.user.id = :userId AND s.finished = false")
//    Optional<ExamSession> findActiveSessionByUserId(@Param("userId") Long userId);
//}
