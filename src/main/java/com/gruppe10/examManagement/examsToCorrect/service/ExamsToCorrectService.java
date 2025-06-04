package com.gruppe10.examManagement.examsToCorrect.service;

import com.gruppe10.examManagement.exam.domain.Exam;
import com.gruppe10.examManagement.exam.domain.ExamRepository;
import com.gruppe10.examManagement.exam.service.ExamService;
import com.gruppe10.examManagement.examAppointment.domain.*;
import com.gruppe10.examManagement.examAppointment.service.ExamAppointmentService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@Service
public class ExamsToCorrectService {

    private final ExamRepository examRepository;
    private final ExamService examService;

    @Autowired
    public ExamsToCorrectService(ExamRepository examRepository,
                                 ExamService examService) {
        this.examRepository = examRepository;
        this.examService = examService;
    }


    //Suche nach ExamAppointments die den Status openToCorrect haben
    public List<Exam> getOpenToCorrectExams() {
        List<Exam> openToCorrectExams = new ArrayList<>();
        examService.getAllExams().forEach(examAppointment -> {
            if (examAppointment.getOpentoCorrect()) {
                openToCorrectExams.add(examAppointment);
            }
        });
        return openToCorrectExams;

    }
    public void releaseResults(Long examId) {
        WebClient.create("/api")
                .post()
                .uri("/instructor/exams/" + examId + "/release")
                .retrieve()
                .toBodilessEntity()
                .block();
    }

}



