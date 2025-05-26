package com.gruppe10.examManagement.exam.domain;

/**
 * Author: Henrik Struckmeier
 * Date: 30/04/2025
 **/


import com.gruppe10.base.domain.AbstractEntity;
import com.gruppe10.examManagement.examAppointment.domain.ExamAppointment;
import com.gruppe10.usermanagement.domain.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import org.jspecify.annotations.Nullable;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "exam")
public class Exam extends AbstractEntity<Long> implements IExamInterface {

    public static final int DESCRIPTION_MAX_LENGTH = 255;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pruefung_id")
    private Long id;

    //Prüfungstitel
    @Column(name = "title", nullable = false, length = DESCRIPTION_MAX_LENGTH)
    @Size(max = DESCRIPTION_MAX_LENGTH)
    private String title;

    //Erstellungsdatum der Prüfung
    @Column(name = "creation_date", nullable = false)
    private Instant creationDate;

    //Das hier soll ein Verweis auf den zugehörigen Lehrer sein
    @ManyToOne
    @JoinColumn(name = "creator_id", referencedColumnName = "user_id")
    private User creator;


    //Hier kann das Modul zu späteren Filterzwecken spezifiziert werden
    @Column(name = "module")
    @Nullable
    private Long module;

    //Das ist ein Wert mit der Gesamtpunktzahl der Prüfung
    @Column(name = "Gesamtpunkte")
    @Nullable
    private double gesamtpunkte;

    //Das ist ein Wert mit einer Bestehensgrenze in Prozent oder Punkten
    @Column(name = "Bestehensgrenze")
    @Nullable
    private double bestehensgrenze;

    //Hier sind die zugehörigen Aufgaben-Ids in einer geordneten Liste gespeichert
    @OneToMany(
            mappedBy = "exam",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("position ASC")
    private List<ExamExercise> examExercises = new ArrayList<>();

    //Beziehung zu den Terminen
    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL)
    private List<ExamAppointment> examAppointments = new ArrayList<>();


    //Getter & Setter folgen
    @Override
    public @Nullable Long getId() {
        return id;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public Instant getCreationDate() {
        return creationDate;
    }

    @Override
    public void setCreationDate(Instant creationDate) {
        this.creationDate = creationDate;
    }


    public @Nullable User getCreator() {
        return creator;
    }

    public void setCreatorId(@Nullable User creator) {
        this.creator = creator;
    }
    public @Nullable Long getCreatorId() {
        return creator != null ? creator.getId() : null;
    }

    @Override
    public void setCreatorId(Long creatorId) {

    }

    public void setModule(@Nullable Long module) {
        this.module = module;
    }

    @Override
    public double getGesamtpunkte() {
        return gesamtpunkte;
    }

    @Override
    public void setGesamtpunkte(double gesamtpunkte) {
        this.gesamtpunkte = gesamtpunkte;
    }

    @Override
    public double getBestehensgrenze() {
        return bestehensgrenze;
    }

    @Override
    public void setBestehensgrenze(double bestehensgrenze) {
        this.bestehensgrenze = bestehensgrenze;
    }


    // Getter und Setter für die Liste mit Terminen
    public List<ExamAppointment> getExamAppointments() {
        return examAppointments;
    }

    public void setExamAppointments(List<ExamAppointment> examAppointments) {
        this.examAppointments = examAppointments;
    }

    // Methode zum Hinzufügen eines Termins
    public void addExamAppointment(ExamAppointment appointment) {
        examAppointments.add(appointment);
        appointment.setExam(this);
    }

    // Methode zum Entfernen eines Termins
    public void removeExamAppointment(ExamAppointment appointment) {
        examAppointments.remove(appointment);
        appointment.setExam(null);
    }


}

