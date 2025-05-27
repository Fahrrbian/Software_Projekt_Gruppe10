package com.gruppe10.examManagement.exam.ui.FormView;

import com.gruppe10.exercisemanagement.domain.Exercise;
import com.gruppe10.exercisemanagement.service.ExerciseService;
import com.gruppe10.taskmanagement.domain.Task;
import com.gruppe10.taskmanagement.service.TaskService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

/**
 * ToDo "Task" durch "Exercise" von Louis tauschen wenn er es hochgeladen hat
 **/

public class ExerciseGrid extends VerticalLayout {
    private final Grid<Exercise> grid;
    private final Button addExerciseBtn;
    private final ExerciseService exerciseService;
    private Long currentPruefungId;

    public ExerciseGrid(ExerciseService exerciseService) {
        this.exerciseService = exerciseService;

        //Grid erstellen
        //Die Columns mit Exercise Daten füllen
        grid = new Grid<>();
        grid.addColumn(Exercise::getExerciseText).setHeader("Aufgabe");
        grid.addColumn(Exercise::getScore).setHeader("Punkte");
        grid.setHeight("200px");
        grid.addItemDoubleClickListener(event -> {
            Exercise exercise = event.getItem();
            if (currentPruefungId != null && exercise != null) {
                exerciseService.assignExerciseToPruefung(exercise.getId(), currentPruefungId);
                refreshData(); // zur Aktualisierung der Anzeige
            }
        });

        // Button erstellen
        addExerciseBtn = new Button("Neue Aufgabe", event -> addNewTask());
        addExerciseBtn.setIcon(new Icon(VaadinIcon.PLUS));
        addExerciseBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        // Layout aufbauen
        add(
                new H3("Aufgaben"),
                new HorizontalLayout(addExerciseBtn),
                grid
        );

        setSpacing(false);
        setPadding(false);
    }

    public void setPruefungId(Long pruefungId) {
        this.currentPruefungId = pruefungId;
        refreshData();
    }

    public void refreshData() {
        if (currentPruefungId != null) {
            grid.setItems(exerciseService.getExerciseForPruefung(currentPruefungId));
        }
    }

    private void addNewTask() {
        if (currentPruefungId != null) {
            Dialog dialog = new Dialog();
            dialog.setHeaderTitle("Neue Aufgabe");

            ExerciseChooseListView exerciseChooseListView = new ExerciseChooseListView(exerciseService, currentPruefungId);

            Button saveButton = new Button("Speichern", e -> {

                /** ToDo: Hier die erstellungslogik für Exercise-Entity hinterlegen
                 **/

            });

            Button cancelButton = new Button("Abbrechen", e -> dialog.close());

            dialog.add(exerciseChooseListView);
            dialog.getFooter().add(cancelButton, saveButton);
            dialog.setWidth("500px");
            dialog.open();
        }
    }
}
