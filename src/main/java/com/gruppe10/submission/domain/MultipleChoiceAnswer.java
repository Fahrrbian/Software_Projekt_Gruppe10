package com.gruppe10.submission.domain;

import java.util.List;

public class MultipleChoiceAnswer implements Answer {
    private String questionId;
    private List<String> selectedOptionIds;
    // + Konstruktor, Getter/Setter

    @Override
    public String getQuestionId() {
        return questionId;
    }

    @Override
    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }
    public List<String> getSelectedOptionIds() {
        return selectedOptionIds;
    }

    public void setSelectedOptionIds(List<String> selectedOptionIds) {
        this.selectedOptionIds = selectedOptionIds;
    }
}
