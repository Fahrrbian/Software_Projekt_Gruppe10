package com.gruppe10.submission.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * AssignmentAnswer.java
 * <p>
 * Created by Fabian Holtapel on 09.06.2025.
 * <p>
 * Description:
 * TODO: Beschreibung einfügen.
 */

public class AssignmentAnswer implements Answer {
    private String questionId;
    private Map<String,String> assignmentMappings = new HashMap<>();

    @Override
    public String getQuestionId() {
        return questionId;
    }

    @Override
    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public Map<String,String> getAssignmentMappings() {
        return assignmentMappings;
    }

    public void setAssignmentMappings(Map<String,String> assignmentMappings) {
        this.assignmentMappings = assignmentMappings;
    }
}
