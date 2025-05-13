package com.gruppe10.exercisemanagement.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AssignmentExerciseRepository extends JpaRepository<AssignmentExercise, Long>, JpaSpecificationExecutor<AssignmentExercise> {
}
