package com.gruppe10.exercisemanagement.domain;

import com.gruppe10.submission.domain.Answer;
import com.gruppe10.submission.domain.SingleChoiceAnswer;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.*;
import java.util.*;

@Entity
@DiscriminatorValue("SingleChoice")
public class SingleChoice extends Exercise{
    @OneToMany(
            mappedBy = "exercise",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    private Set<ChoiceOption> choiceOptions = new HashSet<>();


    @Override
    public double evaluate(Answer answer) {
        if (!(answer instanceof SingleChoiceAnswer scAnswer)) {
            throw new IllegalArgumentException(
                    "Answer must be SingleChoiceAnswer for SingleChoice exercise"
            );
    }
        return choiceOptions.stream()
                .filter(opt -> opt.getId().equals(scAnswer.getSelectedOptionId()))
                .findFirst()
                .map(ChoiceOption::isCorrect)
                .map(correct -> correct ? getScore() : 0.0)  // statt getMaxPoints()
                .orElse(0.0);
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
