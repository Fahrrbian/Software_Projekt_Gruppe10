package com.gruppe10.exercisemanagement.domain;

import com.gruppe10.taskmanagement.domain.Task;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ExerciseRepository extends JpaRepository<Exercise, Long>, JpaSpecificationExecutor<Exercise> {

    Slice<Exercise> findAllBy(Pageable pageable);

    Slice<Exercise> findDistinctByTagsIn(List<Tag> tags, Pageable pageable);

    List<Exercise> findByExam_Id(Long examId);
}
