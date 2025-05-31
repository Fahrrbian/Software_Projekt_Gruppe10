package com.gruppe10.examManagement.exam.domain;


import com.gruppe10.exercisemanagement.domain.Exercise;
import com.gruppe10.taskmanagement.domain.Task;
import com.gruppe10.examManagement.exam.domain.Exam;
import jakarta.persistence.*;

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
/* Ich glaube Task wollten wir ja rausnehmen?
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("taskId")
    private Task task;
*/

    @MapsId("exerciseId")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id")
    private Exercise exercise;

    @Column(name = "position", nullable = false)
    private int position;


    public ExamExercise() {}

    public ExamExercise(Exam IExamInterface, Exercise exercise, int position) {
        this.exam = IExamInterface;
        this.exercise = exercise;
        this.position = position;
        this.id = new ExamExerciseId(IExamInterface.getId(), exercise.getId());
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

    public Exercise getExercise() { return exercise; }
    public void setExercise(Exercise exercise) { this.exercise = exercise; }
}