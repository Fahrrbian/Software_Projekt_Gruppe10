package com.gruppe10.examManagement.exam.domain;

/**
 * Author: Henrik Struckmeier
 * Date: 30/04/2025
 **/

import com.gruppe10.usermanagement.domain.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ExamRepository extends JpaRepository<Exam, Long>, JpaSpecificationExecutor<Exam>{

    // If you don't need a total row count, Slice is better than Page.
    Slice<Exam> findAllBy(Pageable pageable);

    List<Exam> findByCreator(User creator);

    @EntityGraph(attributePaths = {
            "examExercises", "examExercises.exercise"
    })
    @Query("SELECT e FROM Exam e LEFT JOIN FETCH e.examExercises WHERE e.id = :id")
    Optional<Exam> findByIdWithExercises(Long id);


}





