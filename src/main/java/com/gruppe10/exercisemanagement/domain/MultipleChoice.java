package com.gruppe10.exercisemanagement.domain;

import com.gruppe10.submission.domain.Answer;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.*;

import java.util.*;

@Entity
@DiscriminatorValue("MultipleChoice")
public class MultipleChoice extends Exercise{
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "multiple_choice_id")
    private Set<ChoiceOption> choiceOptions = new HashSet<>();

    public Set<ChoiceOption> getChoiceOptions() {
        return choiceOptions;
    }

    public void setChoiceOptions(Set<ChoiceOption> choiceOptions) {
        this.choiceOptions = choiceOptions;
    }

    public void addChoiceOption(ChoiceOption option) {
        choiceOptions.add(option);
    }

    public void removeChoiceOption(ChoiceOption option) {
        choiceOptions.remove(option);
    }

    @Override
    public double evaluate(Answer answer) {
        return 0;
    }
}
