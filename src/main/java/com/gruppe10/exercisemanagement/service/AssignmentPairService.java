package com.gruppe10.exercisemanagement.service;

import com.gruppe10.exercisemanagement.domain.AssignmentPair;
import com.gruppe10.exercisemanagement.domain.AssignmentPairRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AssignmentPairService {

    private final AssignmentPairRepository repository;

    public AssignmentPairService(AssignmentPairRepository repository) {
        this.repository = repository;
    }

    public List<AssignmentPair> getAll() {
        return repository.findAll();
    }

    public Optional<AssignmentPair> getById(Long id) {
        return repository.findById(id);
    }

    public AssignmentPair create(AssignmentPair pair) {
        return repository.save(pair);
    }

    public AssignmentPair update(Long id, AssignmentPair updated) {
        return repository.findById(id)
                .map(existing -> {
                    existing.setPartOne(updated.getPartOne());
                    existing.setPartTwo(updated.getPartTwo());
                    return repository.save(existing);
                })
                .orElseThrow(() -> new IllegalArgumentException("AnswerPair not found"));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
