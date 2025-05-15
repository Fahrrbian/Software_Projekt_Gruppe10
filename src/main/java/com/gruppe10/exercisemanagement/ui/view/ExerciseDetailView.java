package com.gruppe10.exercisemanagement.ui.view;

import com.gruppe10.exercisemanagement.domain.Exercise;
import com.gruppe10.exercisemanagement.service.ExerciseService;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.html.Span;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Optional;

@Route(value = "exercise/:exerciseId", layout = com.gruppe10.base.ui.view.MainLayout.class)
@RolesAllowed("INSTRUCTOR")
public class ExerciseDetailView extends VerticalLayout implements BeforeEnterObserver {

    private final ExerciseService exerciseService;
    private final H2 title = new H2();
    private final Paragraph description = new Paragraph();
    private final Div detailsContainer = new Div(title, description);

    @Autowired
    public ExerciseDetailView(ExerciseService exerciseService) {
        this.exerciseService = exerciseService;
        add(detailsContainer);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        RouteParameters parameters = event.getRouteParameters();
        long exerciseId = Long.parseLong(parameters.get("exerciseId").orElse("-1"));

        Optional<Exercise> exercise = exerciseService.getById(exerciseId);

        if (exercise.isPresent()) {
            displayExerciseDetails(exercise.get());
        } else {
            // Fehlerbehandlung: Aufgabe nicht gefunden
            title.setText("Aufgabe nicht gefunden");
            description.setText("Die angeforderte Aufgabe konnte nicht gefunden werden.");
            detailsContainer.add(title, description);
        }
    }

    private void displayExerciseDetails(Exercise exercise) {
        title.setText("Aufgabe: " + exercise.getExerciseText());
        description.setText("Punkte: " + exercise.getScore());

        Div tagsDiv = new Div();
        tagsDiv.setText("Tags: ");
        HorizontalLayout tagLayout = new HorizontalLayout();
        tagLayout.setAlignItems(FlexComponent.Alignment.BASELINE);
        exercise.getTags().forEach(tag -> {
            Span tagSpan = new Span(tag.getName());
            tagLayout.add(tagSpan);
        });
        tagsDiv.add(tagLayout);
        detailsContainer.add(tagsDiv);
    }
}