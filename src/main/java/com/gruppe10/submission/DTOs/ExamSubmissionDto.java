package com.gruppe10.submission.DTOs;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


import com.gruppe10.exercisemanagement.domain.Exercise;
import com.gruppe10.exercisemanagement.domain.FreetextExercise;
import com.gruppe10.exercisemanagement.domain.MultipleChoice;
import com.gruppe10.exercisemanagement.domain.SingleChoice;
import com.gruppe10.submission.domain.Answer;
import com.gruppe10.submission.domain.FreeTextAnswer;
import com.gruppe10.submission.domain.MultipleChoiceAnswer;
import com.gruppe10.submission.domain.SingleChoiceAnswer;


/**
 * ExamSubmissionDto.java
 * <p>
 * Created by Fabian Holtapel on 30.05.2025.
 * <p>
 * Description:
 * Brücke zwischen Frontend und Domänenmodell (Answer-Objekte).
 * Die Methode toDomainAnswers(Map<String, Exercise> exerciseMap) konvertiert die rawAnswers in fachlich korrekte Domänenobjekte
 * d.h. SCA, MCA; FTA und Exception
 */

public class ExamSubmissionDto {

    private Map<String, String> rawAnswers = new HashMap<>();

    public Map<String, String> getRawAnswers() {
        return rawAnswers;
    }

    public void setRawAnswers(Map<String, String> rawAnswers) {
        this.rawAnswers = rawAnswers;
    }
    public Map<String, Answer> toDomainAnswers(Map<String, Exercise> exerciseMap) {
        return rawAnswers.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> {
                            String qid = e.getKey();
                            String raw = e.getValue();
                            Exercise ex = exerciseMap.get(qid);

                            if (ex instanceof SingleChoice) {
                                // raw ist die optionId
                                SingleChoiceAnswer a = new SingleChoiceAnswer();
                                a.setQuestionId(qid);
                                a.setSelectedOptionId(raw);
                                return a;
                            }
                            else if (ex instanceof MultipleChoice) {
                                MultipleChoiceAnswer a = new MultipleChoiceAnswer();
                                a.setQuestionId(qid);
                                List<String> selected = Arrays.stream(raw.split(","))
                                        .map(String::trim)
                                        .filter(s -> !s.isEmpty())
                                        .collect(Collectors.toList());
                                a.setSelectedOptionIds(selected);
                                return a;
                            }
                            else if (ex instanceof FreetextExercise) {
                                FreeTextAnswer a = new FreeTextAnswer();
                                a.setQuestionId(qid);
                                a.setText(raw);
                                return a;
                            }
                            else {
                                throw new IllegalArgumentException("Unknown exercise type for ID "+qid);
                            }
                        }
                ));
    }
}
