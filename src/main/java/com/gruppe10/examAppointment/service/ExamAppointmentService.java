package com.gruppe10.examAppointment.service;

import com.gruppe10.exam.domain.Exam;
import com.gruppe10.exam.domain.ExamRepository;
import com.gruppe10.examAppointment.domain.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@Service
public class ExamAppointmentService {

    @Autowired
    private ExamAppointmentRepository repository;

    private final ExamAppointmentRepository appointmentRepository;
    private final ExamRepository examRepository;

    @Autowired
    public ExamAppointmentService(ExamAppointmentRepository appointmentRepository,
                                  ExamRepository examRepository) {
        this.appointmentRepository = appointmentRepository;
        this.examRepository = examRepository;
    }

    @Autowired
    private StudentExamAppointmentRepository studentExamAppointmentRepository;


    // Methode zum Speichern der importierten Prüflinge
    public void saveStudentDataForAppointment(Long appointmentId, List<StudentData> studentDataList) {
        ExamAppointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Termin nicht gefunden: " + appointmentId));

        // Bestehende Einträge für diesen Termin löschen
        List<StudentExamAppointment> existingEntries =
                studentExamAppointmentRepository.findByExamAppointmentId(appointmentId);
        studentExamAppointmentRepository.deleteAll(existingEntries);

        // Neue Einträge erstellen und speichern
        List<StudentExamAppointment> newEntries = studentDataList.stream()
                .map(data -> {
                    StudentExamAppointment entry = new StudentExamAppointment();
                    entry.setExamAppointment(appointment);
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
        List<StudentExamAppointment> studentAppointments = studentExamAppointmentRepository
                .findByExamAppointmentId(appointmentId);

        return studentAppointments.stream()
                .map(sea -> new StudentData(
                        sea.getNachname(),
                        sea.getVorname(),
                        sea.getMatrikelnummer()
                ))
                .collect(Collectors.toList());
    }




    // Grundlegende CRUD-Operationen für ExamAppointment Entitys
    public ExamAppointment createAppointment(ExamAppointment appointment, Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new EntityNotFoundException("Prüfung nicht gefunden: " + examId));

        appointment.setExam(exam);
        return appointmentRepository.save(appointment);
    }

    public Optional<ExamAppointment> getAppointmentById(Long id) {
        return appointmentRepository.findById(id);
    }

    public Slice<ExamAppointment> getAllAppointments(Pageable pageable) {
        return appointmentRepository.findAllBy(pageable);
    }

    public ExamAppointment updateAppointment(Long id, ExamAppointment updatedAppointment) {
        ExamAppointment existingAppointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Termin nicht gefunden: " + id));

        existingAppointment.setTitle(updatedAppointment.getTitle());
        existingAppointment.setAppointmentDate(updatedAppointment.getAppointmentDate());

        return appointmentRepository.save(existingAppointment);
    }

    public void deleteAppointment(Long id) {
        if (!appointmentRepository.existsById(id)) {
            throw new EntityNotFoundException("Termin nicht gefunden: " + id);
        }
        appointmentRepository.deleteById(id);
    }


    // Prüfungsspezifische Suche
    public List<ExamAppointment> getAppointmentsByExam(Long examId) {
        return appointmentRepository.findByExamId(examId);
    }

    // Prüfungsspezifische Suche mit DateRange für Filter
    public List<ExamAppointment> getExamAppointmentsInDateRange(Long examId,
                                                                Instant startDate,
                                                                Instant endDate) {
        return appointmentRepository.findByExamIdAndAppointmentDateBetween(examId, startDate, endDate);
    }


}
