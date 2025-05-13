package com.gruppe10.exercisemanagement.service;

import com.gruppe10.exercisemanagement.domain.ChoiceOption;
import com.gruppe10.exercisemanagement.domain.ChoiceOptionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChoiceOptionService {

    private final ChoiceOptionRepository repository;

    public ChoiceOptionService(ChoiceOptionRepository repository) {
        this.repository = repository;
    }

    public List<ChoiceOption> getAll() {
        return repository.findAll();
    }

    public Optional<ChoiceOption> getById(Long id) {
        return repository.findById(id);
    }

    public ChoiceOption create(ChoiceOption option) {
        return repository.save(option);
    }

    public ChoiceOption update(Long id, ChoiceOption updated) {
        return repository.findById(id)
                .map(existing -> {
                    existing.setText(updated.getText());
                    existing.setCorrect(updated.isCorrect());
                    return repository.save(existing);
                })
                .orElseThrow(() -> new IllegalArgumentException("ChoiceOption not found"));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
