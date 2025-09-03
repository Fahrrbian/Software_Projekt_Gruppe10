package com.gruppe10.submission.domain;

import com.gruppe10.examManagement.exam.domain.Exam;
import com.gruppe10.examManagement.examAppointment.domain.StudentExam;
import com.gruppe10.usermanagement.domain.Student;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Submission.java
 * <p>
 * Created by Fabian Holtapel on 14.05.2025.
 * <p>
 * Description:
 * TODO: Beschreibung einfügen.
 */

@Entity
public class Submission {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Exam exam;

    @ManyToOne(optional = false)
    private Student student;

    private Double totalPoints;
    private Boolean passed;

    private Instant submittedAt;

    @ElementCollection
    @CollectionTable(name = "submission_points",
            joinColumns = @JoinColumn(name = "submission_id"))
    @MapKeyColumn(name = "question_id")
    @Column(name = "points")
    private Map<String, Double> aufgabenErgebnisse;

    @Enumerated(EnumType.STRING)
    private SubmissionStatus status;

    @OneToMany(mappedBy = "submission",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<SubmissionAnswer> answers = new ArrayList<>();

    @OneToOne(mappedBy = "submission")
    private StudentExam studentExam;

    public SubmissionStatus getStatus() {
        return status;
    }

    public List<SubmissionAnswer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<SubmissionAnswer> answers) {
        this.answers = answers;
    }

    public void setStatus(SubmissionStatus status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Exam getExam() {
        return exam;
    }

    public void setExam(Exam exam) {
        this.exam = exam;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Double getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(Double totalPoints) {
        this.totalPoints = totalPoints;
    }

    public Boolean getPassed() {
        return passed;
    }

    public void setPassed(Boolean passed) {
        this.passed = passed;
    }

    public Map<String, Double> getAufgabenErgebnisse() {
        return aufgabenErgebnisse;
    }

    public void setAufgabenErgebnisse(Map<String, Double> aufgabenErgebnisse) {
        this.aufgabenErgebnisse = aufgabenErgebnisse;
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(Instant submittedAt) {
        this.submittedAt = submittedAt;
    }

    public StudentExam getStudentExam() {
        return studentExam;
    }

    public void setStudentExam(StudentExam studentExam) {
        this.studentExam = studentExam;
    }

    public double calculateTotal() {
        return aufgabenErgebnisse.values()
                .stream()
                .mapToDouble(Double::doubleValue)
                .sum();
    }
}
