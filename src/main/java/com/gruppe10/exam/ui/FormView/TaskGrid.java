package com.gruppe10.exam.ui.FormView;

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
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;

/**
 * ToDo "Task" durch "Exercise" von Louis tauschen wenn er es hochgeladen hat
 **/

public class TaskGrid extends VerticalLayout {
    private final Grid<Task> grid;
    private final Button addTaskBtn;
    private final TaskService taskService;
    private Long currentPruefungId;

    public TaskGrid(TaskService taskService) {
        this.taskService = taskService;

        //Grid erstellen
        //Die Columns mit Exercise Daten füllen
        grid = new Grid<>();
        grid.addColumn(Task::getDescription).setHeader("Aufgabe");
        grid.addColumn(Task::getDueDate).setHeader("Punkte");
        grid.setHeight("200px");

        // Button erstellen
        addTaskBtn = new Button("Neue Aufgabe", event -> addNewTask());
        addTaskBtn.setIcon(new Icon(VaadinIcon.PLUS));
        addTaskBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        // Layout aufbauen
        add(
                new H3("Aufgaben"),
                new HorizontalLayout(addTaskBtn),
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
            grid.setItems(taskService.getTasksForPruefung(currentPruefungId));
        }
    }

    private void addNewTask() {
        if (currentPruefungId != null) {
            Dialog dialog = new Dialog();
            dialog.setHeaderTitle("Neue Aufgabe");

            ExerciseChooseListView exerciseChooseListView = new ExerciseChooseListView(taskService, currentPruefungId);

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
