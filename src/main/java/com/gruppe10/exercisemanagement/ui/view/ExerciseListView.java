package com.gruppe10.exercisemanagement.ui.view;

import com.gruppe10.exercisemanagement.domain.Exercise;
import com.gruppe10.exercisemanagement.domain.Tag;
import com.gruppe10.exercisemanagement.service.ExerciseService;
import com.gruppe10.exercisemanagement.service.TagService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;
import java.util.stream.Collectors;

@Route("exercises")
@PageTitle("Aufgaben verwalten")
public class ExerciseListView extends Main {

    private final ExerciseService exerciseService;
    private final TagService tagService;

    private final Grid<Exercise> exerciseGrid;
    private final VerticalLayout filterLayout;
    private final Map<Tag, Checkbox> tagFilters = new HashMap<>();

    public ExerciseListView(ExerciseService exerciseService, TagService tagService) {
        this.exerciseService = exerciseService;
        this.tagService = tagService;

        addClassNames(LumoUtility.Padding.MEDIUM, LumoUtility.BoxSizing.BORDER);
        setSizeFull();

        filterLayout = createFilterBar();

        exerciseGrid = new Grid<>(Exercise.class, false);
        configureExerciseGrid();

        Button createButton = new Button("+Neue Aufgabe", event -> createExercise());

        add(filterLayout, createButton, exerciseGrid);
        updateGrid();
    }

    private VerticalLayout createFilterBar() {
        VerticalLayout filterBar = new VerticalLayout();
        filterBar.setPadding(false);
        filterBar.setSpacing(true);

        HorizontalLayout tagCheckboxes = new HorizontalLayout();
        tagCheckboxes.setSpacing(true);

        for (Tag tag : tagService.getAll()) {
            Checkbox tagCheckbox = new Checkbox(tag.getName());
            tagCheckbox.addValueChangeListener(e -> updateGrid());
            tagFilters.put(tag, tagCheckbox);
            tagCheckboxes.add(tagCheckbox);
        }

        filterBar.add(new Span("Filter:"), tagCheckboxes);
        return filterBar;
    }

    private void configureExerciseGrid() {
        exerciseGrid.addColumn(Exercise::getExerciseText).setHeader("Aufgabentext").setAutoWidth(true);
        exerciseGrid.addColumn(Exercise::getScore).setHeader("Punkte");
        exerciseGrid.addColumn(e ->
                e.getTags().stream().map(Tag::getName).collect(Collectors.joining(", "))
        ).setHeader("Tags");
        exerciseGrid.addComponentColumn(this::createEditButton).setHeader("Aktion");

        exerciseGrid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);
        exerciseGrid.setSizeFull();
    }

    private Button createEditButton(Exercise exercise) {
        return new Button("Bearbeiten", click ->
                getUI().ifPresent(ui -> ui.navigate(ExerciseDetailView.class, new RouteParameters("exerciseId", String.valueOf(exercise.getId()))))
        );
    }

    private void updateGrid() {
        List<Tag> selectedTags = tagFilters.entrySet().stream()
                .filter(entry -> entry.getValue().getValue())
                .map(Map.Entry::getKey)
                .toList();

        Pageable pageable = PageRequest.of(0, 100); // z. B. max. 100 Elemente laden

        var slice = selectedTags.isEmpty()
                ? exerciseService.getAll(pageable)
                : exerciseService.getByTags(selectedTags, pageable);

        exerciseGrid.setItems(slice.getContent());
    }

    private void createExercise() {
        getUI().ifPresent(ui -> ui.navigate("create-exercise"));
    }
}

