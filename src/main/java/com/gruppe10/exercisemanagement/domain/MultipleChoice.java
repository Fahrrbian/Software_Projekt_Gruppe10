package com.gruppe10.exercisemanagement.domain;

import com.gruppe10.submission.domain.Answer;
import com.gruppe10.submission.domain.MultipleChoiceAnswer;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.*;

import java.util.*;
import java.util.stream.Collectors;

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
        if (!(answer instanceof MultipleChoiceAnswer)) {
            throw new IllegalArgumentException("Answer ist nicht vom Typ MultipleChoiceAnswer");
        }
        MultipleChoiceAnswer mcAnswer = (MultipleChoiceAnswer) answer;

        Set<String> correctAnswers = choiceOptions.stream()
                .filter(ChoiceOption::isCorrect)
                .map(ChoiceOption::getText)
                .collect(Collectors.toSet());

        Set<String> selectedAnswers = new HashSet<>(mcAnswer.getSelectedOptionIds());

        double maxPoints = this.getScore();
        int correctCount = correctAnswers.size();

        double pointsPerCorrect = maxPoints / correctCount;

        long right = selectedAnswers.stream()
                .filter(correctAnswers::contains)
                .count();

        long wrong = selectedAnswers.stream()
                .filter(rightAnswer -> !correctAnswers.contains(rightAnswer))
                .count();

        double score = pointsPerCorrect * (right - wrong);

        return Math.max(0.0, score);
    }

}
