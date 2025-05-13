package com.gruppe10.exercisemanagement.service;

import com.gruppe10.exercisemanagement.domain.FreetextExercise;
import com.gruppe10.exercisemanagement.domain.FreetextExerciseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FreetextExerciseService {

    private final FreetextExerciseRepository freetextExerciseRepository;

    @Autowired
    public FreetextExerciseService(FreetextExerciseRepository freetextExerciseRepository) {
        this.freetextExerciseRepository = freetextExerciseRepository;
    }

    public FreetextExercise createFreetextExercise(FreetextExercise freetextExercise) {
        return freetextExerciseRepository.save(freetextExercise);
    }

    public Optional<FreetextExercise> findFreetextExerciseById(Long id) {
        return freetextExerciseRepository.findById(id);
    }

    public Page<FreetextExercise> findAllFreetextExercises(Pageable pageable) {
        return freetextExerciseRepository.findAll(pageable);
    }

    public FreetextExercise updateFreetextExercise(Long id, FreetextExercise freetextExercise) {
        if (!freetextExerciseRepository.existsById(id)) {
            return null;
        }
        freetextExercise.setId(id);
        return freetextExerciseRepository.save(freetextExercise);
    }

    public boolean deleteFreetextExercise(Long id) {
        if (!freetextExerciseRepository.existsById(id)) {
            return false;
        }
        freetextExerciseRepository.deleteById(id);
        return true;
    }
}
