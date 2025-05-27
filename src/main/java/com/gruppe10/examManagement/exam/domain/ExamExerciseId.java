package com.gruppe10.examManagement.exam.domain;

import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class ExamExerciseId implements Serializable {
    private Long examId;
    private Long exerciseId;

    public ExamExerciseId() {}

    public ExamExerciseId(Long examId, Long exerciseId) {
        this.examId = examId;
        this.exerciseId = exerciseId;
    }

    // equals und hashCode Implementierung
    // Getter und Setter
}
