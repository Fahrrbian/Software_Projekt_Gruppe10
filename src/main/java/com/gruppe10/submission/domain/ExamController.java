package com.gruppe10.submission.domain;

import com.gruppe10.examManagement.exam.domain.Exam;
import com.gruppe10.examManagement.exam.service.ExamService;
import com.gruppe10.exercisemanagement.domain.Exercise;
import com.gruppe10.submission.DTOs.ExamSubmissionDto;
import com.gruppe10.submission.service.EvaluationService;
import com.gruppe10.submission.service.SubmissionService;
import com.gruppe10.usermanagement.domain.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * ExamController.java
 * <p>
 * Created by Fabian Holtapel on 27.05.2025.
 * <p>
 * Description:
 * TODO: Beschreibung einfügen.
 */
@RestController
@RequestMapping("/api/exams")
public class ExamController {

    private final EvaluationService evalService;
    private final SubmissionService submissionService;
    private final ExamService examService;

    public ExamController(EvaluationService evalService,
                          SubmissionService submissionService, ExamService examService) {
        this.evalService = evalService;
        this.submissionService = submissionService;
        this.examService = examService;
    }

    @PostMapping("/{examId}/submit")
    public ResponseEntity<Submission> submitExam(
            @PathVariable Long examId,
            @RequestBody ExamSubmissionDto payload,
            @AuthenticationPrincipal User user) {

        Optional<Exam> optExam = examService.getById(examId);
        if (optExam.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Exam exam = optExam.get();

        Map<String, Exercise> exerciseMap = exam.getQuestions().stream()
                .collect(Collectors.toMap(
                        ex -> ex.getId().toString(),
                        Function.identity()
                ));

        Map<String, Answer> answers = payload.toDomainAnswers(exerciseMap);

        // 1) auswerten ABER FALLBACK FÜR FT
        ExamResult result = evalService.evaluateExam(exam, answers);
        // 2) speichern
        Submission saved = submissionService.bewerten(
                exam,
                user,
                result.getPerQuestionPoints(),
                payload.getRawAnswers()
        );
        System.out.println(saved+ "---------> Bewerten bei submit");
        return ResponseEntity.ok(saved);
    }

}
