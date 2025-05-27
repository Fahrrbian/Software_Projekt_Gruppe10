/**
 * Author: Christian Markow
 * Date: 27.05.2025
 */

package com.gruppe10.examManagement.exam.ui;

import com.gruppe10.base.ui.Layout.MainLayout;
import com.gruppe10.examManagement.exam.service.ExamService;
import com.gruppe10.exercisemanagement.domain.*;
import com.gruppe10.exercisemanagement.service.ExerciseService;
import com.gruppe10.timer.Timer;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.util.*;

@Route(value = "exam/:examId", layout = MainLayout.class)
@PageTitle("Prüfung")
@RolesAllowed({"INSTRUCTOR", "STUDENT"})
public class ExamExecutionView extends VerticalLayout implements BeforeEnterObserver {

    private final ExerciseService exerciseService;
    private final ExamService examService;

    private List<Exercise> exercises;
    private final Map<Long, Answer> userAnswers = new HashMap<>();
    private int currentIndex = 0;

    private Timer timer;

    public ExamExecutionView(ExerciseService exerciseService, ExamService examService) {
        this.exerciseService = exerciseService;
        this.examService = examService;
        setSizeFull();
        setPadding(true);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        String examId = event.getRouteParameters().get("examId").orElse(null);
        if (examId != null) {
            loadExam(Long.parseLong(examId));
        } else {
            add(new Paragraph("Fehler: Keine Prüfungs-ID angegeben."));
        }
    }

    private void loadExam(Long examId) {
        exercises = exerciseService.getAllByExamId(examId);
        System.out.println("Geladene Aufgaben: " + exercises.size());

        // Starte Timer – z.B. 30 Minuten
        timer = new Timer(30 * 60 * 1000L, this::onTimeUp);
        add(timer);

        showExercise(currentIndex);
    }

    private void showExercise(int index) {
        removeAll();
        add(timer);

        if (index >= exercises.size()) {
            showSummary();
            return;
        }

        Exercise exercise = exercises.get(index);
        add(new H4("Frage " + (index + 1) + ": " + exercise.getExerciseText()));

        Component inputComponent = createInputComponent(exercise);
        add(inputComponent);

        Button nextButton = new Button(index == exercises.size() - 1 ? "Zur Zusammenfassung" : "Weiter", e -> {
            Answer answer = extractAnswerFromComponent(inputComponent, exercise);
            userAnswers.put(exercise.getId(), answer);
            currentIndex++;
            showExercise(currentIndex);
        });

        add(nextButton);
    }

    private Component createInputComponent(Exercise exercise) {
        if (exercise instanceof SingleChoice singleChoice) {
            RadioButtonGroup<String> radio = new RadioButtonGroup<>();
            radio.setItems(singleChoice.getChoiceOptions().stream()
                    .map(ChoiceOption::getText)
                    .toList());
            return radio;
        } else if (exercise instanceof MultipleChoice multipleChoice) {
            CheckboxGroup<String> checkbox = new CheckboxGroup<>();
            checkbox.setItems(multipleChoice.getChoiceOptions().stream()
                    .map(ChoiceOption::getText)
                    .toList());
            return checkbox;
        } else if (exercise instanceof FreetextExercise) {
            TextArea text = new TextArea();
            text.setWidthFull();
            return text;
        } else if (exercise instanceof AssignmentExercise) {
            return new Paragraph("Zuordnungsaufgabe: (noch nicht implementiert)");
        }

        return new Paragraph("Unbekannter Aufgabentyp.");
    }

    private Answer extractAnswerFromComponent(Component comp, Exercise exercise) {
        Answer answer = new Answer();
        answer.setExercise(exercise);

        if (comp instanceof RadioButtonGroup<?>) {
            String selected = ((RadioButtonGroup<String>) comp).getValue();
            answer.setSelectedOptions(List.of(selected));
        } else if (comp instanceof CheckboxGroup<?>) {
            Set<String> selected = ((CheckboxGroup<String>) comp).getValue();
            answer.setSelectedOptions(new ArrayList<>(selected));
        } else if (comp instanceof TextArea) {
            answer.setTextAnswer(((TextArea) comp).getValue());
        }

        return answer;
    }

    private void showSummary() {
        removeAll();
        add(timer);
        add(new H3("Zusammenfassung"));

        for (Exercise ex : exercises) {
            Answer answer = userAnswers.get(ex.getId());
            add(new Paragraph("Frage: " + ex.getExerciseText()));

            if (answer != null) {
                String preview;
                if (ex instanceof SingleChoice || ex instanceof MultipleChoice) {
                    preview = String.join(", ", answer.getSelectedOptions());
                } else if (ex instanceof FreetextExercise) {
                    preview = answer.getTextAnswer();
                } else {
                    preview = "(nicht implementiert)";
                }
                add(new Paragraph("Antwort: " + preview));
            } else {
                add(new Paragraph("Keine Antwort."));
            }

            add(new Hr());
        }

        Button submitButton = new Button("Prüfung abschließen", e -> submitExam());
        add(submitButton);
    }

    private void submitExam() {
        examService.submitAnswers(userAnswers.values());
        Notification.show("Prüfung abgegeben.");
        UI.getCurrent().navigate("user-info");
    }

    private void onTimeUp() {
        Notification.show("Zeit abgelaufen. Prüfung wird abgegeben.");
        submitExam();
    }

}