package com.gruppe10.examManagement.exam.domain;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ExamExerciseId implements Serializable {
    private Long examId;
    private Long taskId;

    public ExamExerciseId() {}

    public ExamExerciseId(Long examId, Long taskId) {
        this.examId = examId;
        this.taskId = taskId;
    }

    public Long getExamId() {
        return examId;
    }

    public void setExamId(Long examId) {
        this.examId = examId;
    }

    public Long getExerciseId() {
        return taskId;
    }

    public void setExerciseId(Long exerciseId) {
        this.taskId = exerciseId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExamExerciseId that = (ExamExerciseId) o;
        return Objects.equals(examId, that.examId) &&
                Objects.equals(taskId, that.taskId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(examId, taskId);
    }

    // equals und hashCode Implementierung
    // Getter und Setter
}
