package com.gruppe10.examManagement.examsToCorrect.service;

import com.gruppe10.examManagement.exam.domain.Exam;
import com.gruppe10.examManagement.exam.domain.ExamRepository;
import com.gruppe10.examManagement.examAppointment.domain.*;
import com.gruppe10.examManagement.examAppointment.service.ExamAppointmentService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@Service
public class ExamsToCorrectService {

    private final ExamAppointmentRepository appointmentRepository;
    private final ExamAppointmentService examAppointmentService;

    @Autowired
    public ExamsToCorrectService(ExamAppointmentRepository appointmentRepository,
                                 ExamAppointmentService examAppointmentService) {
        this.appointmentRepository = appointmentRepository;
        this.examAppointmentService = examAppointmentService;
    }


    //Suche nach ExamAppointments die den Status openToCorrect haben
    public List<ExamAppointment> getOpenToCorrectAppointments() {
        List<ExamAppointment> openToCorrectExams = new ArrayList<>();
        examAppointmentService.getAllAppointments(Pageable.unpaged()).forEach(examAppointment -> {
            if (examAppointment.getOpentoCorrect()) {
                openToCorrectExams.add(examAppointment);
            }
        });
        return openToCorrectExams;

    }

}



