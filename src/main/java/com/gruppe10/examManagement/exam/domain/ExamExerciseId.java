package com.gruppe10.examManagement.exam.domain;

import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class ExamExerciseId implements Serializable {
    private Long examId;
    private Long taskId;

    public ExamExerciseId() {}

    public ExamExerciseId(Long examId, Long taskId) {
        this.examId = examId;
        this.taskId = taskId;
    }

    // equals und hashCode Implementierung
    // Getter und Setter
}
