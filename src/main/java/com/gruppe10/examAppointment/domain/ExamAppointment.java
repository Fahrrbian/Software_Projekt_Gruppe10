package com.gruppe10.examAppointment.domain;

import com.gruppe10.base.domain.AbstractEntity;
import com.gruppe10.exam.domain.Exam;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import org.jspecify.annotations.Nullable;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: Henrik Struckmeier
 * Date: 30/04/2025
 **/

/**
 * Diese Klasse ist die Beziehung zwischen einer Prüfung und mehreren Terminen
 * **/

@Entity
@Table(name = "exam_appointment")
public class ExamAppointment extends AbstractEntity<Long> {

    public static final int DESCRIPTION_MAX_LENGTH = 255;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pruefung_id")
    private Long id;

    @Column(name = "title", nullable = false, length = DESCRIPTION_MAX_LENGTH)
    @Size(max = DESCRIPTION_MAX_LENGTH)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @Column(name = "appointment_date", nullable = false)
    private Instant appointmentDate;

    @OneToMany(mappedBy = "examAppointment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudentExamAppointment> studentExamAppointments = new ArrayList<>();

    public List<StudentExamAppointment> getStudentExamAppointments() {
        return studentExamAppointments;
    }

    public void setStudentExamAppointments(List<StudentExamAppointment> studentExamAppointments) {
        this.studentExamAppointments = studentExamAppointments;
    }



    // Getter und Setter folgen
    @Override
    public @Nullable Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Exam getExam() {
        return exam;
    }

    public void setExam(Exam exam) {
        this.exam = exam;
    }

    public Instant getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(Instant appointmentDate) {
        this.appointmentDate = appointmentDate;
    }
}



