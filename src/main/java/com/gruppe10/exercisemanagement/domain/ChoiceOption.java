package com.gruppe10.exercisemanagement.domain;

import com.gruppe10.base.domain.AbstractEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "choice_option")
public class ChoiceOption extends AbstractEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "choice_option_id")
    private Long id;

    @Column(name = "text", nullable = false)
    private String text;

    @Column(name = "correct", nullable = false)
    private boolean isCorrect;

    @ManyToOne
    @JoinColumn(name = "exercise_id")
    private Exercise exercise;

    public ChoiceOption() {
    }

    public ChoiceOption(String text, boolean isCorrect, Exercise exercise) {
        this.text = text;
        this.isCorrect = isCorrect;
        this.exercise = exercise;
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    public Exercise getExercise() {
        return exercise;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }
}
