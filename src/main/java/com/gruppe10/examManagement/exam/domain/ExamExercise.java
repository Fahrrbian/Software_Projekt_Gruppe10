package com.gruppe10.examManagement.exam.domain;


import com.gruppe10.exercisemanagement.domain.Exercise;
import com.gruppe10.taskmanagement.domain.Task;
import jakarta.persistence.*;

@Entity
@Table(name = "exam_exercise")
public class ExamExercise {
    @EmbeddedId
    private ExamExerciseId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("examId")
    private Exam exam;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("exerciseId")
    private Exercise exercise;

    @Column(name = "position")
    private int position;



    // Konstruktoren
    public ExamExercise() {}

    public ExamExercise(Exam IExamInterface, Exercise exercise, int position) {
        this.exam = IExamInterface;
        this.exercise = exercise;
        this.position = position;
        this.id = new ExamExerciseId(IExamInterface.getId(), exercise.getId());
    }

    // Getter und Setter
    // ...
}