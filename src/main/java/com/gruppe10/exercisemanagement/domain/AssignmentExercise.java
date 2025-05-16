package com.gruppe10.exercisemanagement.domain;

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
}
