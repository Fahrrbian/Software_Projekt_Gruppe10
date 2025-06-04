///**
// * Author: Christian Markow
// * Date: 27.05.2025
// */
//
//package com.gruppe10.examManagement.exam.service;
//
//import com.gruppe10.examManagement.exam.domain.Exam;
//import com.gruppe10.examManagement.exam.domain.ExamSession;
//import com.gruppe10.examManagement.exam.domain.ExamSessionRepository;
//import com.gruppe10.examManagement.examAppointment.domain.ExamAppointment;
//import com.gruppe10.usermanagement.domain.User;
//import org.springframework.stereotype.Service;
//
//import java.time.Instant;
//import java.util.Optional;
//
//@Service
//public class ExamSessionService {
//
//    private final ExamSessionRepository examSessionRepository;
//
//    public ExamSessionService(ExamSessionRepository examSessionRepository) {
//        this.examSessionRepository = examSessionRepository;
//    }
//
//    public ExamSession createSession(Exam exam, ExamAppointment examAppointment, User user, long duration) {
//        ExamSession session = new ExamSession(exam, examAppointment, user, Instant.now(), duration);
//        return examSessionRepository.save(session);
//    }
//
//    public ExamSession getSessionByExamId(Long examId) {
//        return examSessionRepository.findByExamId(examId).orElse(null);
//    }
//
//    public ExamSession getSessionByExamIdAndUser(Long examId, Long userId) {
//        return examSessionRepository.findByExamIdAndUserId(examId, userId).orElse(null);
//    }
//
//    public ExamSession getSessionByAppointmentIdAndUser(Long appointmentId, Long userId) {
//        return examSessionRepository.findByAppointmentIdAndUserId(appointmentId, userId).orElse(null);
//    }
//
//    public ExamSession getActiveSessionByUserId(Long userId) {
//        return examSessionRepository.findActiveSessionByUserId(userId).orElse(null);
//    }
//
//}
//
