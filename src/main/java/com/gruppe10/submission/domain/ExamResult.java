package com.gruppe10.submission.domain;

import java.util.Map;

/**
 * ExamResult.java
 * <p>
 * Created by Fabian Holtapel on 27.05.2025.
 * <p>
 * Description:
 * TODO: Beschreibung einfügen.
 */

public class ExamResult {
    private final Map<String, Double> perQuestionPoints;
    private final double totalPoints;
    private final boolean passed;

    public ExamResult(Map<String, Double> perQuestionPoints,
                      double totalPoints,
                      boolean passed) {
        this.perQuestionPoints = perQuestionPoints;
        this.totalPoints = totalPoints;
        this.passed = passed;
    }

    public Map<String, Double> getPerQuestionPoints() {
        return perQuestionPoints;
    }

    public double getTotalPoints() {
        return totalPoints;
    }

    public boolean isPassed() {
        return passed;
    }
}
