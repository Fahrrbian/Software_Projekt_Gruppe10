package com.gruppe10.exercisemanagement.service;

import com.gruppe10.exercisemanagement.domain.SingleChoice;
import com.gruppe10.exercisemanagement.domain.SingleChoiceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SingleChoiceService {

    private final SingleChoiceRepository repository;

    public SingleChoiceService(SingleChoiceRepository repository) {
        this.repository = repository;
    }

    public List<SingleChoice> getAll() {
        return repository.findAll();
    }

    public Optional<SingleChoice> getById(Long id) {
        return repository.findById(id);
    }

    public SingleChoice create(SingleChoice exercise) {
        return repository.save(exercise);
    }

    public SingleChoice update(Long id, SingleChoice updated) {
        return repository.findById(id)
                .map(existing -> {
                    existing.setScore(updated.getScore());
                    existing.setExerciseText(updated.getExerciseText());
                    existing.setTags(updated.getTags());
                    return repository.save(existing);
                })
                .orElseThrow(() -> new IllegalArgumentException("SingleChoice not found"));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
