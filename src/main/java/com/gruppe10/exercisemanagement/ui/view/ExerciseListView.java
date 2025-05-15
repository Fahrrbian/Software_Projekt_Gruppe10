package com.gruppe10.exercisemanagement.ui.view;

import com.gruppe10.base.ui.view.MainLayout;
import com.gruppe10.exercisemanagement.domain.Exercise;
import com.gruppe10.exercisemanagement.domain.Tag;
import com.gruppe10.exercisemanagement.service.ExerciseService;
import com.gruppe10.exercisemanagement.service.TagService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;

import java.util.*;
import java.util.stream.Collectors;

@Route(value="exercises", layout = MainLayout.class)
@PageTitle("Aufgaben verwalten")
@RolesAllowed("INSTRUCTOR")
public class ExerciseListView extends Main {

    private final ExerciseService exerciseService;
    private final TagService tagService;

    private final Grid<Exercise> exerciseGrid;
    private final VerticalLayout filterLayout;

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
        updateGrid(null);
    }

    private VerticalLayout createFilterBar() {
        VerticalLayout filterBar = new VerticalLayout();
        filterBar.setPadding(false);
        filterBar.setSpacing(true);

        MultiSelectComboBox<Tag> tagFilterComboBox = new MultiSelectComboBox<>("Tags filtern");
        tagFilterComboBox.setItemLabelGenerator(Tag::getName);
        tagFilterComboBox.setItems(tagService.getAll());
        tagFilterComboBox.setClearButtonVisible(true);
        tagFilterComboBox.setPlaceholder("Tags auswählen...");
        tagFilterComboBox.setWidth("300px");

        tagFilterComboBox.addValueChangeListener(event -> updateGrid(event.getValue()));

        filterBar.add(tagFilterComboBox);
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

    private void updateGrid(Set<Tag> selectedTags) {
        Pageable pageable = PageRequest.of(0, 100);

        var slice = (selectedTags == null || selectedTags.isEmpty())
                ? exerciseService.getAll(pageable)
                : exerciseService.getByTags(new ArrayList<>(selectedTags), pageable);

        exerciseGrid.setItems(slice.getContent());
    }

    private void createExercise() {
        getUI().ifPresent(ui -> ui.navigate("create-exercise"));
    }
}

