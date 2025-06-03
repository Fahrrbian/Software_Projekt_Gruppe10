package com.gruppe10.exercisemanagement.domain;

import com.gruppe10.submission.domain.Answer;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("FreetextExercise")
public class FreetextExercise extends Exercise{
    @Override
    public double evaluate(Answer answer) {
        return 0;
    }
}
