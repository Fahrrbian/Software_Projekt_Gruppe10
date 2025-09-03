//package com.gruppe10.examManagement.exam.domain;
//
//import jakarta.persistence.Column;
//import jakarta.persistence.Embeddable;
//
//import java.io.Serializable;
//import java.util.Objects;
//
//@Embeddable
//public class ExamExerciseId implements Serializable {
//
//    @Column(name = "exercise_id")
//    private Long exerciseId;
//
//    @Column(name = "pruefung_id")
//    private Long examId;
//
//    public ExamExerciseId() {}
//
//    public ExamExerciseId(Long examId, Long exerciseId) {
//        this.examId = examId;
//        this.exerciseId = exerciseId;
//    }
//
//    public Long getExamId() {
//        return examId;
//    }
//
//    public void setExamId(Long examId) {
//        this.examId = examId;
//    }
//
//    public Long getExerciseId() {
//        return exerciseId;
//    }
//
//    public void setExerciseId(Long exerciseId) {
//        this.exerciseId = exerciseId;
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (!(o instanceof ExamExerciseId)) return false;
//        ExamExerciseId that = (ExamExerciseId) o;
//        return Objects.equals(examId, that.examId) &&
//                Objects.equals(exerciseId, that.exerciseId);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(examId, exerciseId);
//    }
//
//}