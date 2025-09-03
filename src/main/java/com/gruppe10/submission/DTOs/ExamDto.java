package com.gruppe10.submission.DTOs;

import com.gruppe10.examManagement.exam.domain.Exam;
import com.gruppe10.exercisemanagement.domain.Exercise;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ExamDto.java
 * <p>
 * Created by Fabian Holtapel on 30.05.2025.
 * <p>
 * Description:
 * TODO: Beschreibung einfügen.
 */

public class ExamDto {   private Long examId;
    private String title;
    private Instant creationDate;
    private boolean hasFreeTextQuestions;
    private boolean autoPublishResults;
    private List<QuestionDto> questions = new ArrayList<>();


    public static ExamDto from(Exam exam) {
        ExamDto dto = new ExamDto();
        dto.examId               = exam.getId();
        dto.title                = exam.getTitle();
        dto.creationDate         = exam.getCreationDate();
        dto.hasFreeTextQuestions = exam.isHasFreeTextQuestions();
        dto.autoPublishResults   = exam.isAutoPublishResults();

        dto.questions = exam.getQuestions().stream()//Ohne Comparator werden die Ergebnisse unsortiert gemappt...
                .map(QuestionDto::from)
                .collect(Collectors.toList());

        return dto;
    }

    public Long getExamId() {
        return examId;
    }

    public void setExamId(Long examId) {
        this.examId = examId;
    }

    public Instant getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Instant creationDate) {
        this.creationDate = creationDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isAutoPublishResults() {
        return autoPublishResults;
    }

    public void setAutoPublishResults(boolean autoPublishResults) {
        this.autoPublishResults = autoPublishResults;
    }

    public boolean isHasFreeTextQuestions() {
        return hasFreeTextQuestions;
    }

    public void setHasFreeTextQuestions(boolean hasFreeTextQuestions) {
        this.hasFreeTextQuestions = hasFreeTextQuestions;
    }

    public List<QuestionDto> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionDto> questions) {
        this.questions = questions;
    }

}
