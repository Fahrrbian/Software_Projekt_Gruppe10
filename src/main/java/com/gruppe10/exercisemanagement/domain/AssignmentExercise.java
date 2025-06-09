package com.gruppe10.exercisemanagement.domain;

import com.gruppe10.submission.domain.Answer;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.*;

import java.util.*;


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
        return 0;
    }

}