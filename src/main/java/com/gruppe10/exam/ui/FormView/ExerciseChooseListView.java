package com.gruppe10.exam.ui.FormView;

import com.gruppe10.taskmanagement.domain.Task;
import com.gruppe10.taskmanagement.service.TaskService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class ExerciseChooseListView extends VerticalLayout {
    private final Grid<Task> grid;
    private final TaskService taskService;
    private final Long currentPruefungId;


    public ExerciseChooseListView(TaskService taskService, Long pruefungId) {
        this.taskService = taskService;
        this.currentPruefungId = pruefungId;

        grid = new Grid<>();
        grid.addColumn(Task::getDescription).setHeader("Aufgabe");
        grid.addColumn(Task::getDueDate).setHeader("Punkte");
        grid.setHeight("200px");

        add(grid);
        setSpacing(false);
        setPadding(false);
        refreshData();
    }


    public void refreshData() {
            grid.setItems(taskService.getAllTasks());
    }
}
