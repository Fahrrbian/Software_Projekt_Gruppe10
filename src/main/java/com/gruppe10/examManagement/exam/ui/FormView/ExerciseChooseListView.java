package com.gruppe10.examManagement.exam.ui.FormView;

import com.gruppe10.base.ui.Layout.MainLayout;
import com.gruppe10.exercisemanagement.domain.Exercise;
import com.gruppe10.exercisemanagement.service.ExerciseService;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public class ExerciseChooseListView extends VerticalLayout {
    private final Grid<Exercise> grid;
    private final ExerciseService exerciseService;
    private Exercise selectedExercise;
    private final Long currentPruefungId;

    public ExerciseChooseListView(ExerciseService exerciseService, Long pruefungId) {
        this.exerciseService = exerciseService;
        this.currentPruefungId = pruefungId;

        grid = new Grid<>();
        grid.addColumn(Exercise::getExerciseText).setHeader("Aufgabe");
        grid.addColumn(Exercise::getScore).setHeader("Punkte");
        grid.setHeight("200px");

        grid.asSingleSelect().addValueChangeListener(e -> {
            selectedExercise = e.getValue();
        });

        add(grid);
        setSpacing(false);
        setPadding(false);
        refreshData();
    }

    public void refreshData() {
        Pageable pageable = PageRequest.of(0, 10);
        Slice<Exercise> slice = exerciseService.getAll(pageable);
        grid.setItems(slice.getContent());
    }

    public Exercise getSelectedExercise() {
        return selectedExercise;
    }

}
