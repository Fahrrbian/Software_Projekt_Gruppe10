package com.gruppe10.submission.domain;

import com.gruppe10.examManagement.exam.domain.Exam;
import com.gruppe10.usermanagement.domain.Student;
import jakarta.persistence.*;

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

    @ElementCollection
    private Map<String, Double> aufgabenErgebnisse;

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
}
