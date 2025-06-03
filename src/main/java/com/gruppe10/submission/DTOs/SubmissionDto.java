package com.gruppe10.submission.DTOs;

import com.gruppe10.submission.domain.Submission;
import com.gruppe10.submission.domain.SubmissionAnswer;
import com.gruppe10.submission.domain.SubmissionStatus;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * SubmissionDto.java
 * <p>
 * Created by Fabian Holtapel on 30.05.2025.
 * <p>
 * Description:
 * SubmissionDto ist das Response-DTO, das an den Client zurückgeschicht wird, sobald die Auswertung/Persistenz durch ist.
 */

public class SubmissionDto {
    private Long submissionId;
    private Long examId;
    private String email;
    private Map<String, String> rawAnswers;
    private Map<String, Double> perQuestionPoints;
    private double totalPoints;
    private boolean passed;
    private SubmissionStatus status;
    private Instant submittedAt;


    public static SubmissionDto from(Submission sub) {
        SubmissionDto dto = new SubmissionDto();
        dto.setSubmissionId(sub.getId());
        dto.setExamId(sub.getExam().getId());
        dto.setEmail(sub.getStudent().getEmail());
        // rawAnswers aus Submission sub.getAnswers()
        Map<String,String> raws = sub.getAnswers().stream()
                .collect(Collectors.toMap(
                        SubmissionAnswer::getQuestionId,
                        SubmissionAnswer::getAnswerData
                ));
        dto.setRawAnswers(raws);

        dto.setPerQuestionPoints(sub.getAufgabenErgebnisse());
        dto.setTotalPoints(sub.getTotalPoints());
        dto.setPassed(sub.getPassed());
        dto.setStatus(sub.getStatus());
        dto.setSubmittedAt(sub.getSubmittedAt());
        return dto;
    }

    public Long getExamId() {
        return examId;
    }

    public void setExamId(Long examId) {
        this.examId = examId;
    }

    public Long getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(Long submissionId) {
        this.submissionId = submissionId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Map<String, String> getRawAnswers() {
        return rawAnswers;
    }

    public void setRawAnswers(Map<String, String> rawAnswers) {
        this.rawAnswers = rawAnswers;
    }

    public Map<String, Double> getPerQuestionPoints() {
        return perQuestionPoints;
    }

    public void setPerQuestionPoints(Map<String, Double> perQuestionPoints) {
        this.perQuestionPoints = perQuestionPoints;
    }

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    public SubmissionStatus getStatus() {
        return status;
    }

    public void setStatus(SubmissionStatus status) {
        this.status = status;
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(Instant submittedAt) {
        this.submittedAt = submittedAt;
    }

    public double getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(double totalPoints) {
        this.totalPoints = totalPoints;
    }

}
