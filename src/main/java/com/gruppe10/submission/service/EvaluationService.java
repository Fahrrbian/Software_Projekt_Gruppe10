package com.gruppe10.submission.service;

import com.gruppe10.examManagement.exam.domain.Exam;
import com.gruppe10.exercisemanagement.domain.Exercise;
import com.gruppe10.submission.domain.Answer;
import com.gruppe10.submission.domain.ExamResult;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * EvaluationService.java
 * <p>
 * Created by Fabian Holtapel on 27.05.2025.
 * <p>
 * Description:
 * TODO: Beschreibung einfügen.
 */
@Service
public class EvaluationService {
    public ExamResult evaluateExam(Exam exam, Map<String, Answer> answers) {
        Map<String, Double> perQuestionPoints = new HashMap<>();

        for (Exercise exercise : exam.getQuestions()) {
            String questionId = exercise.getId().toString();
            Answer answer = answers.get(questionId);

            double points = 0.0;

            if (answer != null)
                points = exercise.evaluate(answer);

            perQuestionPoints.put(questionId, points);
        }

        double total = perQuestionPoints.values()
                .stream()
                .mapToDouble(Double::doubleValue)
                .sum();

        boolean passed = total >= exam.getBestehensgrenze();

        return new ExamResult(perQuestionPoints, total, passed);
    }
}
