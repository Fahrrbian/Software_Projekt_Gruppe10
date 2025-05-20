package com.gruppe10.exam.domain;


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
    @MapsId("taskId")
    private Task task;

    @Column(name = "position")
    private int position;



    // Konstruktoren
    public ExamExercise() {}

    public ExamExercise(Exam IExamInterface, Task task, int position) {
        this.exam = IExamInterface;
        this.task = task;
        this.position = position;
        this.id = new ExamExerciseId(IExamInterface.getId(), task.getId());
    }

    // Getter und Setter
    // ...
}