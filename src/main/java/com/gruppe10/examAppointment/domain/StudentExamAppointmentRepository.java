package com.gruppe10.examAppointment.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentExamAppointmentRepository extends JpaRepository<StudentExamAppointment, Long> {
    List<StudentExamAppointment> findByExamAppointmentId(Long examAppointmentId);
}