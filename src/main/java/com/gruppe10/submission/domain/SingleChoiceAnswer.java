package com.gruppe10.submission.domain;

public class SingleChoiceAnswer implements Answer {
    private String questionId;
    private String selectedOptionId;
    // + Konstruktor, Getter/Setter

    @Override
    public String getQuestionId() {
        return questionId;
    }

    @Override
    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }
    public String getSelectedOptionId() {
        return selectedOptionId;
    }

    public void setSelectedOptionId(String selectedOptionId) {
        this.selectedOptionId = selectedOptionId;
    }
}
