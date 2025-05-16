package com.gruppe10.exercisemanagement.domain;

import jakarta.persistence.*;
import com.gruppe10.base.domain.AbstractEntity;

@Entity
@Table(name = "assignment_pair")
public class AssignmentPair extends AbstractEntity<Long>{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pair_id")
    private Long id;

    @Column(name = "part_one", nullable = false)
    private String partOne;

    @Column(name = "part_two", nullable = false)
    private String partTwo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_exercise_id", nullable = false)
    private AssignmentExercise assignmentExercise;

    public Long getId() {
        return id;
    }

    public String getPartOne() {
        return partOne;
    }

    public void setPartOne(String partOne) {
        this.partOne = partOne;
    }

    public String getPartTwo() {
        return partTwo;
    }

    public void setPartTwo(String partTwo) {
        this.partTwo = partTwo;
    }

    public AssignmentExercise getAssignmentExercise() {
        return assignmentExercise;
    }

    public void setAssignmentExercise(AssignmentExercise assignmentExercise) {
        this.assignmentExercise = assignmentExercise;
    }
}
