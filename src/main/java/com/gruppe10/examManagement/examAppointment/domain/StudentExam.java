package com.gruppe10.examManagement.examAppointment.domain;

import com.gruppe10.base.domain.AbstractEntity;
import com.gruppe10.examManagement.exam.domain.Exam;
import com.gruppe10.submission.domain.Submission;
import com.gruppe10.usermanagement.domain.Student;
import jakarta.persistence.*;
import org.jspecify.annotations.Nullable;

import java.time.LocalDateTime;


/**
 * Diese Klasse ist die Beziehung zwischen einem appointment und Prüflingen
 * <p>
 * ToDo:
 **/

@Entity
@Table(name = "student_exam")
public class StudentExam extends AbstractEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    @Column(name = "nachname", nullable = false)
    private String nachname;

    @Column(name = "vorname", nullable = false)
    private String vorname;

    @Column(name = "matrikelnummer")
    private String matrikelnummer;

    @Column(name = "gesperrt")
    private boolean gesperrt = false;

    @Column(name = "completed")
    private boolean completed = false;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "submission_id", referencedColumnName = "id")
    private Submission submission;

    //Getter und Setter
    @Override
    public @Nullable Long getId() {
        return id;
    }

    public Exam getExam() {
        return exam;
    }

    public void setExam(Exam exam) {
        this.exam = exam;
    }

    public String getNachname() {
        return nachname;
    }

    public void setNachname(String nachname) {
        this.nachname = nachname;
    }

    public String getVorname() {
        return vorname;
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    public String getMatrikelnummer() {
        return matrikelnummer;
    }

    public void setMatrikelnummer(String matrikelnummer) {
        this.matrikelnummer = matrikelnummer;
    }

    public boolean isGesperrt() {
        return gesperrt;
    }

    public void setGesperrt(boolean gesperrt) {
        this.gesperrt = gesperrt;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Submission getSubmission() {return submission;}

    public void setSubmission(Submission submission) {this.submission = submission;}

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }
}