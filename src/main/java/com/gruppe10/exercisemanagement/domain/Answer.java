/**
 * Author: Christian Markow
 * Date: 27.05.2025
 */

package com.gruppe10.exercisemanagement.domain;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Exercise exercise;

    @ElementCollection
    private List<String> selectedOptions = new ArrayList<>();

    private String textAnswer;

    @ElementCollection
    @CollectionTable(name = "assignment_mappings", joinColumns = @JoinColumn(name = "answer_id"))
    @MapKeyColumn(name = "left_side")
    @Column(name = "right_side")
    private Map<String, String> assignmentMappings = new HashMap<>();

    //Optional: ID eines Benutzers, um Antwort zuzuordnen
    private Long userId;

    public Long getId() {
        return id;
    }

    public Exercise getExercise() {
        return exercise;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

    public List<String> getSelectedOptions() {
        return selectedOptions;
    }

    public void setSelectedOptions(List<String> selectedOptions) {
        this.selectedOptions = selectedOptions;
    }

    public String getTextAnswer() {
        return textAnswer;
    }

    public void setTextAnswer(String textAnswer) {
        this.textAnswer = textAnswer;
    }

    public Map<String, String> getAssignmentMappings() {
        return assignmentMappings;
    }

    public void setAssignmentMappings(Map<String, String> assignmentMappings) {
        this.assignmentMappings = assignmentMappings;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}

