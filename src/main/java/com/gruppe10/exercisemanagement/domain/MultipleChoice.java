package com.gruppe10.exercisemanagement.domain;

import com.gruppe10.submission.domain.Answer;
import com.gruppe10.submission.domain.MultipleChoiceAnswer;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import org.jspecify.annotations.Nullable;

import java.util.*;

@Entity
@DiscriminatorValue("MultipleChoice")
public class MultipleChoice extends Exercise{
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "multiple_choice_id")
    private Set<ChoiceOption> choiceOptions = new HashSet<>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
        if (!(answer instanceof MultipleChoiceAnswer)) {
            throw new IllegalArgumentException("Answer ist nicht vom Typ MultipleChoiceAnswer");
        }
        MultipleChoiceAnswer mcAnswer = (MultipleChoiceAnswer) answer;


        Set<String> correctIds = new HashSet<>();
        for (ChoiceOption opt : choiceOptions) {
            if (opt.isCorrect()) {
                correctIds.add(opt.getId().toString());
            }
        }

        // Die vom Studenten gewählten Option-IDs:
        Set<String> selectedIds = new HashSet<>(mcAnswer.getSelectedOptionIds()); // List<String> oder Set<String>


        if (selectedIds.equals(correctIds)) {
            return this.getScore();
        } else {

            //  Keine Teilpunkte:
            return 0.0;
            //
            //  Teilpunkte vergeben :
            //   double maxPoints = this.getScore();
            //   long richtigGewählt = selectedIds.stream().filter(correctIds::contains).count();
            //   return maxPoints * ((double) richtigGewählt / correctIds.size());
        }
    }

    @Override
    public @Nullable Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }
}
