package com.gruppe10.examManagement.exam.domain;


import com.gruppe10.exercisemanagement.domain.Exercise;
import com.gruppe10.taskmanagement.domain.Task;
import jakarta.persistence.*;
import org.jspecify.annotations.Nullable;

@Entity
@Table(name = "exam_exercise")
public class ExamExercise {
    @EmbeddedId
    private ExamExerciseId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("examId")
    private Exam exam;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @MapsId("taskId")
//    private Task task;

    @ManyToOne
    @JoinColumn(name = "exercise_id")
    private Exercise exercise;

    @Column(name = "position")
    private int position;



    // Konstruktoren
    public ExamExercise() {this.id = new ExamExerciseId();}

    public ExamExercise(Exam IExamInterface, Exam exam, int position) {
        this.exam = exam;
        this.position = position;
        this.id = new ExamExerciseId(IExamInterface.getId(), exam.getId());
    }

    public @Nullable ExamExerciseId getId() {
        return id;
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