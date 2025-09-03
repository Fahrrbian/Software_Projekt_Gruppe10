package com.gruppe10.exercisemanagement.domain;

import com.gruppe10.submission.domain.Answer;
import com.gruppe10.submission.domain.AssignmentAnswer;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.*;

import java.util.*;
import java.util.stream.Collectors;


@Entity
@DiscriminatorValue("AssignentExercise")
public class AssignmentExercise extends Exercise{

    @OneToMany(mappedBy = "assignmentExercise", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<AssignmentPair> assignmentPairs = new HashSet<>();

    public Set<AssignmentPair> getAssignmentPairs() {
        return assignmentPairs;
    }

    public void setAssignmentPairs(Set<AssignmentPair> assignmentPairs) {
        this.assignmentPairs = assignmentPairs;
    }

    public void addAssignmentPair(AssignmentPair pair) {
        assignmentPairs.add(pair);
        pair.setAssignmentExercise(this);
    }

    public void removeAssignmentPair(AssignmentPair pair) {
        assignmentPairs.remove(pair);
        pair.setAssignmentExercise(null);
    }

    @Override
    public double evaluate(Answer answer) {
        if (!(answer instanceof AssignmentAnswer assignAnswer)) {
            throw new IllegalArgumentException("Answer ist nicht vom Typ AssignmentAnswer");
        }

        Map<String, String> userMappings = assignAnswer.getAssignmentMappings();
        if (userMappings == null) {
            userMappings = Map.of();
        }

        // Korrekte Paare als Set
        Set<Map.Entry<String, String>> correctPairs = assignmentPairs.stream()
                .map(pair -> Map.entry(pair.getPartOne(), pair.getPartTwo()))
                .collect(Collectors.toSet());

        int totalPairs = correctPairs.size();
        if (totalPairs == 0) {
            return 0;
        }

        int correctCount = 0;

        for (Map.Entry<String, String> userEntry : userMappings.entrySet()) {
            String userKey = userEntry.getKey();
            String userValue = userEntry.getValue();
            Map.Entry<String, String> pair = Map.entry(userKey, userValue);

            if (correctPairs.contains(pair))
                correctCount++;
        }

        double maxScore = getScore();
        double finalScore = ((double) correctCount / totalPairs) * maxScore;

        return finalScore;
    }

}