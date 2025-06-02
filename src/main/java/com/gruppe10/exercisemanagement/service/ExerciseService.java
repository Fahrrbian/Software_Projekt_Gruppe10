package com.gruppe10.exercisemanagement.service;

import com.gruppe10.exercisemanagement.domain.Exercise;
import com.gruppe10.exercisemanagement.domain.ExerciseRepository;
import com.gruppe10.exercisemanagement.domain.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ExerciseService {

    private final ExerciseRepository repository;

    public ExerciseService(ExerciseRepository repository) {
        this.repository = repository;
    }

    public Slice<Exercise> getAll(Pageable pageable) {
        return repository.findAllBy(pageable);
    }

    public Optional<Exercise> getById(Long id) {
        return repository.findById(id);
    }

    public Exercise create(Exercise exercise) {
        return repository.save(exercise);
    }

    public Exercise update(Long id, Exercise updated) {
        return repository.findById(id)
                .map(existing -> {
                    existing.setScore(updated.getScore());
                    existing.setExerciseText(updated.getExerciseText());
                    existing.setTags(updated.getTags());
                    return repository.save(existing);
                })
                .orElseThrow(() -> new IllegalArgumentException("Exercise not found"));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Slice<Exercise> getByTags(List<Tag> tags, Pageable pageable) {
        if (tags == null || tags.isEmpty()) {
            return getAll(pageable);
        }
        return repository.findDistinctByTagsIn(tags, pageable);
    }
}
