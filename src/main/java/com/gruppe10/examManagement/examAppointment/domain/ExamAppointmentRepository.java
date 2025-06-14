//package com.gruppe10.examManagement.examAppointment.domain;
//
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Slice;
//import org.springframework.data.jpa.repository.JpaRepository;
//import java.time.Instant;
//import java.util.List;
//import java.util.Optional;
//
//public interface ExamAppointmentRepository extends JpaRepository<ExamAppointment, Long> {
//
//    Slice<ExamAppointment> findAllBy(Pageable pageable);
//
//
//    List<ExamAppointment> findByExamId(Long examId);
//
//    List<ExamAppointment> findByExamIdAndAppointmentDateBetween(Long examId, Instant appointmentDateAfter, Instant appointmentDateBefore);
//
//    Optional<ExamAppointment> findById(Long appointmentId);
//}
