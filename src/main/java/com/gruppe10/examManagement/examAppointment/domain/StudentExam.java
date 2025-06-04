package com.gruppe10.examManagement.examAppointment.domain;

import com.gruppe10.base.domain.AbstractEntity;
import com.gruppe10.examManagement.exam.domain.Exam;
import com.gruppe10.submission.domain.Submission;
import jakarta.persistence.*;
import org.jspecify.annotations.Nullable;


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

    @Column(name = "nachname", nullable = false)
    private String nachname;

    @Column(name = "vorname", nullable = false)
    private String vorname;

    @Column(name = "matrikelnummer", nullable = false)
    private String matrikelnummer;

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

    public Submission getSubmission() {return submission;}

    public void setSubmission(Submission submission) {this.submission = submission;}

}