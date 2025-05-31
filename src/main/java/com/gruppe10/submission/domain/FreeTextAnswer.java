package com.gruppe10.submission.domain;

/**
 * FreeTextAnswer.java
 * <p>
 * Created by Fabian Holtapel on 30.05.2025.
 * <p>
 * Description:
 * TODO: Beschreibung einfügen.
 */

public class FreeTextAnswer implements Answer {
    private String questionId;
    private String text;

    @Override
    public String getQuestionId() {
        return questionId;
    }

    @Override
    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
