package com.gruppe10.examManagement.exam.ui.FormView;

import com.gruppe10.examManagement.exam.domain.Exam;
import com.gruppe10.examManagement.exam.domain.ExamExercise;
import com.gruppe10.examManagement.exam.service.ExamService;
import com.gruppe10.exercisemanagement.domain.Exercise;
import com.gruppe10.exercisemanagement.service.ExerciseService;
import com.gruppe10.taskmanagement.domain.Task;
import com.gruppe10.taskmanagement.service.TaskService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public class ExerciseChooseListView extends VerticalLayout {
    private final Grid<Exercise> grid;
    private final ExerciseService exerciseService;
    private final ExamService examService;
    private final Long currentPruefungId;


    public ExerciseChooseListView(ExerciseService exerciseService, ExamService examService, Long pruefungId) {
        this.exerciseService = exerciseService;
        this.examService = examService;
        this.currentPruefungId = pruefungId;

        grid = new Grid<>();
        grid.addColumn(Exercise::getId).setHeader("Aufgabe");
        grid.addColumn(Exercise::getExerciseText).setHeader("Punkte");
        grid.setHeight("200px");

        // Doppelklick-Handler hinzufügen
        grid.addItemDoubleClickListener(event -> {
            Exercise selectedExercise = event.getItem();
            if (selectedExercise != null) {
                try {
                    examService.addExerciseToExam(currentPruefungId, selectedExercise);

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

}
