package com.gruppe10.submission.DTOs;

import com.gruppe10.exercisemanagement.domain.Exercise;
import com.gruppe10.exercisemanagement.domain.MultipleChoice;
import com.gruppe10.exercisemanagement.domain.SingleChoice;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * QuestionDto.java
 * <p>
 * Created by Fabian Holtapel on 30.05.2025.
 * <p>
 * Description:
 * TODO: Beschreibung einfügen.
 */

public class QuestionDto {
    public enum Type { SINGLE_CHOICE, MULTIPLE_CHOICE, FREE_TEXT }

    private String questionId;
    private String text;
    private int score;
    private Type type;
    private List<OptionDto> options;  // nur für Choice-Fragen

    public static QuestionDto from(Exercise ex) {
        QuestionDto dto = new QuestionDto();
        dto.questionId = ex.getId().toString();
        dto.text       = ex.getExerciseText();
        dto.score      = ex.getScore();

        if (ex instanceof SingleChoice sc) {
            dto.type    = Type.SINGLE_CHOICE;
            dto.options = sc.getChoiceOptions().stream()
                    .map(o -> new OptionDto(o.getId().toString(), o.getText()))
                    .collect(Collectors.toList());
        }
        else if (ex instanceof MultipleChoice mc) {
            dto.type    = Type.MULTIPLE_CHOICE;
            dto.options = mc.getChoiceOptions().stream()
                    .map(o -> new OptionDto(o.getId().toString(), o.getText()))
                    .collect(Collectors.toList());
        }
        else {
            dto.type    = Type.FREE_TEXT;
            dto.options = Collections.emptyList();
        }
        return dto;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public List<OptionDto> getOptions() {
        return options;
    }

    public void setOptions(List<OptionDto> options) {
        this.options = options;
    }

}

