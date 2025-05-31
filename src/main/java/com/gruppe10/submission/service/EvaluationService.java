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

        for (Exercise q : exam.getQuestions()) {
/*            Answer a = answers.get(q.getId());
            double pts = q.evaluate(a);
            perQuestionPoints.put(q.getId(), pts); Wie soll das funktionieren, wenn in der perQuestionPoints ein String Wert in der map erwartet wird? */
            String questionId = q.getId().toString();
            Answer a = answers.get(q.getId());
            double pts = q.evaluate(a);
            perQuestionPoints.put(questionId, pts);

        }

        double total = perQuestionPoints.values()
                .stream()
                .mapToDouble(Double::doubleValue)
                .sum();
        boolean passed = total >= exam.getBestehensgrenze();

        return new ExamResult(perQuestionPoints, total, passed);
    }
}
