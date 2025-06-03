package com.gruppe10.examManagement.exam.domain;


import com.gruppe10.exercisemanagement.domain.Exercise;
import com.gruppe10.taskmanagement.domain.Task;
import com.gruppe10.examManagement.exam.domain.Exam;
import jakarta.persistence.*;
import org.jspecify.annotations.Nullable;

import java.io.Serializable;
import java.util.stream.Collectors;

@Entity
@Table(name = "exam_exercise")
public class ExamExercise {

    @EmbeddedId
    private ExamExerciseId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("examId")
    @JoinColumn(name = "pruefung_id")
    private Exam exam;

    @MapsId("exerciseId")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id")
    private Exercise exercise;

    @Column(name = "position", nullable = false)
    private int position;

    // Konstruktoren
    public ExamExercise() {this.id = new ExamExerciseId();}

    public ExamExercise(Exam IExamInterface, Exercise exercise, int position) {
        this.exam = IExamInterface;
        this.exercise = exercise;
    }
    
    public ExamExercise(Exam IExamInterface, Exam exam, int position) {
        this.exam = exam;
        this.position = position;
        this.id = new ExamExerciseId(IExamInterface.getId(), exercise.getId());
        //this.id = new ExamExerciseId(IExamInterface.getId(), exam.getId());
    }

    public ExamExerciseId getId() {
        return id;
    }

    public void setId(ExamExerciseId id) {
        this.id = id;
    }

    public Exam getExam() {
        return exam;
    }

    public void setExam(Exam exam) {
        this.exam = exam;
    }
    
    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Exercise getExercise() { 
        return exercise; 
    }
    
    public Exam getExam() {
        return exam;
    }

    public void setExam(Exam exam) {
        this.exam = exam;
        if (this.id == null) {
            this.id = new ExamExerciseId();
        }
        if (exam != null) {
            this.id.setExamId(exam.getId());
        }
    }

    public Exercise getExercise() {
        return exercise;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
        if (this.id == null) {
            this.id = new ExamExerciseId();
        }
        if (exercise != null) {
            this.id.setExerciseId(exercise.getId());
        }
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

}