package com.gruppe10.examManagement.examAppointment.domain;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.Instant;
import java.util.List;

public interface ExamAppointmentRepository extends JpaRepository<ExamAppointment, Long> {

    // If you don't need a total row count, Slice is better than Page.
    //↑ Übernommen aus Vaadin.start
    Slice<ExamAppointment> findAllBy(Pageable pageable);


    List<ExamAppointment> findByExamId(Long examId);

    List<ExamAppointment> findByExamIdAndAppointmentDateBetween(Long examId, Instant appointmentDateAfter, Instant appointmentDateBefore);

}
