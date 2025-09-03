package com.gruppe10.exercisemanagement.domain;

import com.gruppe10.submission.domain.Answer;
import com.gruppe10.submission.domain.SingleChoiceAnswer;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.*;
import org.jspecify.annotations.Nullable;

import java.util.*;

@Entity
@DiscriminatorValue("SingleChoice")
public class SingleChoice extends Exercise {

    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    @JoinColumn(name = "single_choice_id")
    private Set<ChoiceOption> choiceOptions = new HashSet<>();


    @Override
    public double evaluate(Answer answer) {
        if (!(answer instanceof SingleChoiceAnswer scAnswer)) {
            throw new IllegalArgumentException("Answer ist nicht vom Typ SingleChoiceAnswer");
        }

        String selected = scAnswer.getSelectedOptionId();

        for (ChoiceOption opt : choiceOptions) {
            if (opt.getText().equals(selected)) {
                return opt.isCorrect() ? getScore() : 0.0;
            }
        }

        return 0.0;
    }

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

}
