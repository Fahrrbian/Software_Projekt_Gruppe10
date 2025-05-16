package com.gruppe10.exercisemanagement.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FreetextExerciseRepository extends JpaRepository<FreetextExercise, Long>, JpaSpecificationExecutor<FreetextExercise> {
}
