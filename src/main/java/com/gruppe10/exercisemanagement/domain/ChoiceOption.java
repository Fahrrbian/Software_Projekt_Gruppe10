package com.gruppe10.exercisemanagement.domain;

import com.gruppe10.base.domain.AbstractEntity;
import jakarta.persistence.*;

@Entity
public class ChoiceOption extends AbstractEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "option_text", nullable = false)
    private String text;

    @Column(name = "is_correct", nullable = false)
    private boolean isCorrect;

    // Kein Bezug zurück zu SingleChoice oder MultipleChoice!

    public Long getId() { return id; }

    public String getText() { return text; }

    public void setText(String text) { this.text = text; }

    public boolean isCorrect() { return isCorrect; }

    public void setCorrect(boolean correct) { isCorrect = correct; }
}
