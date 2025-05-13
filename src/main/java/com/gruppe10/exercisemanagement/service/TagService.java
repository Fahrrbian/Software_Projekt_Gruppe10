package com.gruppe10.exercisemanagement.service;

import com.gruppe10.exercisemanagement.domain.Tag;
import com.gruppe10.exercisemanagement.domain.TagRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TagService {

    private final TagRepository repository;

    public TagService(TagRepository repository) {
        this.repository = repository;
    }

    public List<Tag> getAll() {
        return repository.findAll();
    }

    public Optional<Tag> getById(Long id) {
        return repository.findById(id);
    }

    public Tag create(Tag tag) {
        return repository.save(tag);
    }

    public Tag update(Long id, Tag updated) {
        return repository.findById(id)
                .map(existing -> {
                    existing.setName(updated.getName());
                    return repository.save(existing);
                })
                .orElseThrow(() -> new IllegalArgumentException("Tag not found"));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}

