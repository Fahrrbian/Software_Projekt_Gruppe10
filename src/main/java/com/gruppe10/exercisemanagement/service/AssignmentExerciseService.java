package com.gruppe10.exercisemanagement.service;

import com.gruppe10.exercisemanagement.domain.AssignmentExercise;
import com.gruppe10.exercisemanagement.domain.AssignmentExerciseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AssignmentExerciseService {

    private final AssignmentExerciseRepository repository;

    public AssignmentExerciseService(AssignmentExerciseRepository repository) {
        this.repository = repository;
    }

    public List<AssignmentExercise> getAll() {
        return repository.findAll();
    }

    public Optional<AssignmentExercise> getById(Long id) {
        return repository.findById(id);
    }

    public AssignmentExercise create(AssignmentExercise exercise) {
        return repository.save(exercise);
    }

    public AssignmentExercise update(Long id, AssignmentExercise updated) {
        return repository.findById(id)
                .map(existing -> {
                    existing.setScore(updated.getScore());
                    existing.setExerciseText(updated.getExerciseText());
                    existing.setTags(updated.getTags());
                    return repository.save(existing);
                })
                .orElseThrow(() -> new IllegalArgumentException("AssignmentExercise not found"));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
