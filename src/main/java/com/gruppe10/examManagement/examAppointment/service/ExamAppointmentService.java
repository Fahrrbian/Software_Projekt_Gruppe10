package com.gruppe10.examManagement.examAppointment.service;

import com.gruppe10.examManagement.exam.domain.Exam;
import com.gruppe10.examManagement.exam.domain.ExamRepository;
import com.gruppe10.examManagement.examAppointment.domain.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@Service
public class ExamAppointmentService {

    @Autowired
    private final ExamRepository examRepository;

    @Autowired
    public ExamAppointmentService(ExamRepository examRepository) {
        this.examRepository = examRepository;
    }

    @Autowired
    private StudentExamRepository studentExamAppointmentRepository;


    // Methode zum Speichern der importierten Prüflinge
    public void saveStudentDataForExam(Long examId, List<StudentData> studentDataList) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new EntityNotFoundException("Termin nicht gefunden: " + examId));

        // Bestehende Einträge für diesen Termin löschen
        List<StudentExam> existingEntries =
                studentExamAppointmentRepository.findByExamId(examId);
        studentExamAppointmentRepository.deleteAll(existingEntries);

        // Neue Einträge erstellen und speichern
        List<StudentExam> newEntries = studentDataList.stream()
                .map(data -> {
                    StudentExam entry = new StudentExam();
                    entry.setExam(exam);
                    entry.setNachname(data.getNachname());
                    entry.setVorname(data.getVorname());
                    entry.setMatrikelnummer(data.getMatrikelnummer());
                    return entry;
                })
                .collect(Collectors.toList());

        studentExamAppointmentRepository.saveAll(newEntries);
    }

    //Methode zum Laden der Studentendaten
    public List<StudentData> getStudentDataForAppointment(Long appointmentId) {
        List<StudentExam> studentAppointments = studentExamAppointmentRepository
                .findByExamId(appointmentId);

        return studentAppointments.stream()
                .map(sea -> new StudentData(
                        sea.getNachname(),
                        sea.getVorname(),
                        sea.getMatrikelnummer()
                ))
                .collect(Collectors.toList());
    }



    public Optional<Exam> findById(Long examId) {
        return examRepository.findById(examId);
    }
}