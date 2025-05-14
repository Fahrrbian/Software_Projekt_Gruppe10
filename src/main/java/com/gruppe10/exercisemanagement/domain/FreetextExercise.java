package com.gruppe10.exercisemanagement.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("FreetextExercise")
public class FreetextExercise extends Exercise{
}
