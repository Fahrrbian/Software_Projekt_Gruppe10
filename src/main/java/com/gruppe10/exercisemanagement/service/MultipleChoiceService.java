package com.gruppe10.exercisemanagement.service;

import com.gruppe10.exercisemanagement.domain.MultipleChoice;
import com.gruppe10.exercisemanagement.domain.MultipleChoiceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MultipleChoiceService {

    private final MultipleChoiceRepository repository;

    public MultipleChoiceService(MultipleChoiceRepository repository) {
        this.repository = repository;
    }

    public List<MultipleChoice> getAll() {
        return repository.findAll();
    }

    public Optional<MultipleChoice> getById(Long id) {
        return repository.findById(id);
    }

    public MultipleChoice create(MultipleChoice exercise) {
        return repository.save(exercise);
    }

    public MultipleChoice update(Long id, MultipleChoice updated) {
        return repository.findById(id)
                .map(existing -> {
                    existing.setScore(updated.getScore());
                    existing.setExerciseText(updated.getExerciseText());
                    existing.setTags(updated.getTags());
                    return repository.save(existing);
                })
                .orElseThrow(() -> new IllegalArgumentException("MultipleChoice not found"));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
