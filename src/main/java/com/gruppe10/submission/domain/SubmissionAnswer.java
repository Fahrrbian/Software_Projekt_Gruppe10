package com.gruppe10.submission.domain;

import jakarta.persistence.*;

/**
 * SubmissionAnswer.java
 * <p>
 * Created by Fabian Holtapel on 27.05.2025.
 * <p>
 * Description:
 * Eigenes Entity für die Auswertung einzlender Antworten
 */
@Entity
public class SubmissionAnswer {
    @Id
    @GeneratedValue
    private Long   id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id")
    private Submission submission;

    private String questionId;

    @Lob
    private String answerData;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Submission getSubmission() {
        return submission;
    }

    public void setSubmission(Submission submission) {
        this.submission = submission;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getAnswerData() {
        return answerData;
    }

    public void setAnswerData(String answerData) {
        this.answerData = answerData;
    }
}
