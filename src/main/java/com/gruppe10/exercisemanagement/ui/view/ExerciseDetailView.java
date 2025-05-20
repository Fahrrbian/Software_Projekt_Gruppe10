package com.gruppe10.exercisemanagement.ui.view;

import com.gruppe10.exercisemanagement.domain.*;
import com.gruppe10.exercisemanagement.service.ExerciseService;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.*;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

@Route(value = "exercise/:exerciseId", layout = com.gruppe10.base.ui.view.MainLayout.class)
@RolesAllowed("INSTRUCTOR")
public class ExerciseDetailView extends VerticalLayout implements BeforeEnterObserver {

    private final ExerciseService exerciseService;

    private final H2 title = new H2();

    @Autowired
    public ExerciseDetailView(ExerciseService exerciseService) {
        this.exerciseService = exerciseService;
        setPadding(true);
        setSpacing(true);
        setWidthFull();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        RouteParameters parameters = event.getRouteParameters();
        long exerciseId = Long.parseLong(parameters.get("exerciseId").orElse("-1"));

        Optional<Exercise> exercise = exerciseService.getById(exerciseId);

        if (exercise.isPresent()) {
            displayExerciseDetails(exercise.get());
        } else {
            add(new H2("Aufgabe nicht gefunden"));
            add(new Paragraph("Die angeforderte Aufgabe konnte nicht gefunden werden."));
        }
    }

    private void displayExerciseDetails(Exercise exercise) {
        title.setText("Aufgabe: " + exercise.getExerciseText());
        add(title);

        Paragraph score = new Paragraph("Punkte: " + exercise.getScore());
        add(score);

        add(new H3("Tags"));
        FlowLayout tagLayout = new FlowLayout();
        exercise.getTags().forEach(tag -> {
            Span tagChip = new Span(tag.getName());
            tagChip.getStyle()
                    .set("background-color", "#e0e0e0")
                    .set("padding", "4px 8px")
                    .set("border-radius", "8px")
                    .set("font-size", "small");
            tagLayout.add(tagChip);
        });
        add(tagLayout);

        if (exercise instanceof SingleChoice singleChoice) {
            add(new H3("Aufgabentyp: Single Choice"));
            showChoiceOptions(singleChoice.getChoiceOptions().stream().toList());
        } else if (exercise instanceof MultipleChoice multipleChoice) {
            add(new H3("Aufgabentyp: Multiple Choice"));
            showChoiceOptions(multipleChoice.getChoiceOptions().stream().toList());
        } else {
            add(new H3("Aufgabentyp: " + exercise.getClass().getSimpleName()));
        }
    }

    private void showChoiceOptions(List<ChoiceOption> options) {
        VerticalLayout optionsLayout = new VerticalLayout();
        optionsLayout.setPadding(false);
        optionsLayout.setSpacing(false);

        for (ChoiceOption option : options) {
            Span optionSpan = new Span((option.isCorrect() ? "✔ " : "❌ ") + option.getText());
            optionSpan.getStyle()
                    .set("color", option.isCorrect() ? "green" : "gray")
                    .set("font-size", "small");
            optionsLayout.add(optionSpan);
        }

        add(optionsLayout);
    }

    private static class FlowLayout extends Div {
        public FlowLayout() {
            getStyle().set("display", "flex");
            getStyle().set("flex-wrap", "wrap");
            getStyle().set("gap", "0.5em");
        }
    }
}