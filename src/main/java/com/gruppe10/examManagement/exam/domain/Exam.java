package com.gruppe10.examManagement.exam.domain;

/**
 * Author: Henrik Struckmeier
 * Date: 30/04/2025
 **/

import com.gruppe10.base.domain.AbstractEntity;
import com.gruppe10.examManagement.examAppointment.domain.StudentExam;
import com.gruppe10.exercisemanagement.domain.Exercise;
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


    //Flags für Submission-Flow um bei reinen MC/SC direkt zu publishen und Freitext vorhanden
    @Column(name = "auto_publish_results", nullable = false,
            columnDefinition = "BOOLEAN DEFAULT false")
    private boolean autoPublishResults;

    @Column(name = "has_free_text_questions", nullable = false,
            columnDefinition = "BOOLEAN DEFAULT false")
    private boolean hasFreeTextQuestions;

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

    // Felder von ExamAppointment
    @Column(name = "appointment_date")
    private Instant appointmentDate;

    @Column(name = "gesperrt")
    private Boolean gesperrt = false;

    @Column(name = "openToCorrect")
    private boolean openToCorrect;


    //Hier sind die zugehörigen Aufgaben-Ids in einer geordneten Liste gespeichert
    @ManyToMany(
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            fetch = FetchType.EAGER
    )
    @JoinTable(
            name = "exam_exercise",
            joinColumns = @JoinColumn(name = "exam_id"),
            inverseJoinColumns = @JoinColumn(name = "exercise_id")
    )
    private List<Exercise> exercises = new ArrayList<>();


    public List<Exercise> getQuestions() {
        return exercises;
    }

    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<StudentExam> studentExamAppointments = new ArrayList<>();


    // Hilfsmethoden für StudentExamAppointment
    public void addStudentExamAppointment(StudentExam appointment) {
        studentExamAppointments.add(appointment);
        appointment.setExam(this);
    }

    public void removeStudentExamAppointment(StudentExam appointment) {
        studentExamAppointments.remove(appointment);
        appointment.setExam(null);
    }


    //Getter & Setter folgen
    @Override
    public @Nullable Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Exercise> getExercises() {
        return exercises;
    }

    public void setExercises(List<Exercise> exercises) {
        this.exercises = exercises;
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
    public boolean isAutoPublishResults() {
        return autoPublishResults;
    }

    public void setAutoPublishResults(boolean autoPublishResults) {
        this.autoPublishResults = autoPublishResults;
    }

    public boolean isHasFreeTextQuestions() {
        return hasFreeTextQuestions;
    }

    public void setHasFreeTextQuestions(boolean hasFreeTextQuestions) {
        this.hasFreeTextQuestions = hasFreeTextQuestions;
    }

    public void addExercise(Exercise exercise) {
        if (!this.exercises.contains(exercise)) {
            this.exercises.add(exercise);
//            exercise.addExam(this);
        }
    }

    public void removeExercise(Exercise exercise) {
        this.exercises.remove(exercise);
    }

    //  Getter und Setter für die ExamAppointment Felder
    public Instant getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(Instant appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public boolean isGesperrt() {
        return gesperrt;
    }

    public void setGesperrt(boolean gesperrt) {
        this.gesperrt = gesperrt;
    }

    public List<StudentExam> getStudentExamAppointments() {
        return studentExamAppointments;
    }

    public void setStudentExamAppointments(List<StudentExam> studentExamAppointments) {
        this.studentExamAppointments = studentExamAppointments;
    }

    public boolean getOpentoCorrect() {
        if (!studentExamAppointments.isEmpty()) {
            studentExamAppointments.forEach(studentExamAppointment -> {
                try {
                    if (studentExamAppointment.getSubmission().getPassed() == null) {
                        openToCorrect = true;
                    }
                } catch (Exception e) {
                    openToCorrect = true;
                }
            });
        }
        return openToCorrect;
    }

    public void setOpenToCorrect(boolean openToCorrect) {
        this.openToCorrect = openToCorrect;
    }

    public void removeAllStudentExamAppointments() {
        studentExamAppointments.clear();
    }
}