package com.gruppe10.examManagement.examAppointment.domain;

import com.gruppe10.base.domain.AbstractEntity;
import com.gruppe10.submission.domain.Submission;
import jakarta.persistence.*;
import org.jspecify.annotations.Nullable;


/**
 * Diese Klasse ist die Beziehung zwischen einem appointment und Prüflingen
 * <p>
 * ToDo:
 **/

@Entity
@Table(name = "student_exam_appointment")
public class StudentExamAppointment extends AbstractEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_appointment_id", nullable = false)
    private ExamAppointment examAppointment;

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

    public ExamAppointment getExamAppointment() {
        return examAppointment;
    }

    public void setExamAppointment(ExamAppointment examAppointment) {
        this.examAppointment = examAppointment;
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