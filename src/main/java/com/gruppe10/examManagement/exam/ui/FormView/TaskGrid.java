package com.gruppe10.examManagement.exam.ui.FormView;

import com.gruppe10.examManagement.exam.service.ExamService;
import com.gruppe10.exercisemanagement.domain.Exercise;
import com.gruppe10.exercisemanagement.service.ExerciseService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.springframework.data.domain.Pageable;


public class TaskGrid extends VerticalLayout {
    private final Grid<Exercise> grid;
    private final Button addTaskBtn;
    private final ExerciseService exerciseService;
    public final ExamService examService;
    private Long currentPruefungId;

    public TaskGrid(ExerciseService exerciseService,ExamService examService) {
        this.exerciseService = exerciseService;
        this.examService = examService;

        //Grid erstellen
        //Die Columns mit Exercise Daten füllen
        grid = new Grid<>();
        grid.addColumn(Exercise::getId).setHeader("Aufgabe");
        grid.addColumn(Exercise::getScore).setHeader("Punkte");
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
//            grid.setItems(exerciseService.getByExam(currentPruefungId));
            grid.setItems(exerciseService.getAll(Pageable.unpaged()).getContent());
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
