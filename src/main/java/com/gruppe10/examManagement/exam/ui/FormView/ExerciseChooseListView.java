package com.gruppe10.examManagement.exam.ui.FormView;

import com.gruppe10.examManagement.exam.domain.Exam;
import com.gruppe10.examManagement.exam.service.ExamService;
import com.gruppe10.exercisemanagement.domain.Exercise;
import com.gruppe10.exercisemanagement.service.ExerciseService;
import com.gruppe10.taskmanagement.domain.Task;
import com.gruppe10.taskmanagement.service.TaskService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.gruppe10.base.ui.Layout.MainLayout;
import com.gruppe10.exercisemanagement.domain.Exercise;
import com.gruppe10.exercisemanagement.service.ExerciseService;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;


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

        // Doppelklick-Handler hinzufügen
        grid.addItemDoubleClickListener(event -> {
            Exercise selectedExercise = event.getItem();
            if (selectedExercise != null) {
                try {
                    exerciseService.assignExerciseToPruefung(selectedExercise.getId(), currentPruefungId );

                    // Erfolgsmeldung anzeigen
                    Notification.show("Aufgabe wurde zur Prüfung hinzugefügt",
                                    3000, Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

                    // Dialog schließen, falls vorhanden
                    getParent().ifPresent(parent -> {
                        if (parent instanceof Dialog) {
                            ((Dialog) parent).close();
                        }
                    });

                } catch (Exception e) {
                    Notification.show("Fehler beim Hinzufügen der Aufgabe: " + e.getMessage(),
                                    30000, Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            }
        });


        add(grid);
        setSpacing(false);
        setPadding(false);
        refreshData();
    }

    public void refreshData() {
        grid.setItems(exerciseService.getAll(Pageable.unpaged()).getContent());
    }

    public Exercise getSelectedExercise() {
        return selectedExercise;
    }

}
