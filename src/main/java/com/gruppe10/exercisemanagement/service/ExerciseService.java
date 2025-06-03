package com.gruppe10.exercisemanagement.service;

import com.gruppe10.examManagement.exam.domain.Exam;
import com.gruppe10.examManagement.exam.domain.ExamRepository;
import com.gruppe10.exercisemanagement.domain.Exercise;
import com.gruppe10.exercisemanagement.domain.ExerciseRepository;
import com.gruppe10.exercisemanagement.domain.Tag;
import com.gruppe10.taskmanagement.domain.Task;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ExerciseService {

    private final ExerciseRepository repository;
    private final ExamRepository examRepository;

    public ExerciseService(ExerciseRepository repository, ExamRepository examRepository) {
        this.repository = repository;
        this.examRepository = examRepository;
    }

    public Slice<Exercise> getAll(Pageable pageable) {
        return repository.findAllBy(pageable);
    }

    public List<Exercise> getExerciseForPruefung(Long examId){
        return repository.findAll();
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

    public List<Exercise> getAllByExamId(Long examId) {
        return repository.findByExam_Id(examId);
    }
    @Transactional
    public void assignExerciseToPruefung(Long exerciseId, Long examId) {
        Exercise exercise = repository.findById(exerciseId)
                .orElseThrow(() -> new RuntimeException("Aufgabe nicht gefunden"));
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Prüfung nicht gefunden"));

        exercise.addExam(exam);
        repository.save(exercise);
    }

}
