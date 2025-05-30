package com.gruppe10.submission.service;

import com.gruppe10.submission.domain.Submission;

/**
 * SubmissionSubmittedEvent.java
 * <p>
 * Created by Fabian Holtapel on 27.05.2025.
 * <p>
 * Description:
 * Hilfsklasse für Submissions die abegeben wurden
 */

public class SubmissionSubmittedEvent {
    private final Submission submission;
    public SubmissionSubmittedEvent(Object source, Submission submission) {
        super();
        this.submission = submission;
    }
    public Submission getSubmission() {
        return submission;
    }
}
