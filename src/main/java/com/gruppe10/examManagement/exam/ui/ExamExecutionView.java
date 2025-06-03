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
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.*;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.List;

@Route(value = "exam/:examId", layout = MainLayout.class)
@PageTitle("Prüfung")
@RolesAllowed({"INSTRUCTOR", "STUDENT"})
public class ExamExecutionView extends VerticalLayout implements BeforeEnterObserver {

    private final ExerciseService exerciseService;
    private final ExamService examService;

    private List<Exercise> exercises = new ArrayList<>();
    private Map<Long, Answer> userAnswers = new HashMap<>();

    private int currentIndex = 0;
    private Component currentComponent;
    private Timer timer;

    @Autowired
    public ExamExecutionView(ExerciseService exerciseService, ExamService examService) {
        this.exerciseService = exerciseService;
        this.examService = examService;
        setSpacing(true);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        String examIdParam = event.getRouteParameters().get("examId").orElse(null);
        if (examIdParam != null) {
            try {
                long examId = Long.parseLong(examIdParam);
                loadExam(examId);
            } catch (NumberFormatException e) {
                showError("Ungültige Prüfung-ID.");
            }
        } else {
            showError("Fehler: Prüfung nicht gefunden.");
        }
    }

    private void loadExam(Long examId) {
        exercises = exerciseService.getAllByExamId(examId);

        timer = new Timer(30 * 60 * 1000L, this::onTimeUp); // 30 Minuten
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

        currentComponent = createInputComponent(exercise);
        add(currentComponent);

        HorizontalLayout navButtons = getHorizontalLayout(index);
        add(navButtons);
    }

    private HorizontalLayout getHorizontalLayout(int index) {
        HorizontalLayout navButtons = new HorizontalLayout();

        Button prevButton = new Button("Zurück", e -> {
            saveAnswer();
            if (currentIndex > 0) {
                currentIndex--;
                showExercise(currentIndex);
            }
        });
        prevButton.setEnabled(index > 0);

        Button nextButton = new Button(index == exercises.size() - 1 ? "Zur Zusammenfassung" : "Weiter", e -> {
            saveAnswer();
            currentIndex++;
            showExercise(currentIndex);
        });

        navButtons.add(prevButton, nextButton);
        return navButtons;
    }

    private Component createInputComponent(Exercise exercise) {
        Answer saved = userAnswers.get(exercise.getId());

        if (exercise instanceof SingleChoice singleChoice) {
            RadioButtonGroup<String> radio = new RadioButtonGroup<>();
            radio.setItems(singleChoice.getChoiceOptions().stream().map(ChoiceOption::getText).toList());
            if (saved != null && saved.getSelectedOptions() != null && !saved.getSelectedOptions().isEmpty()) {
                radio.setValue(saved.getSelectedOptions().get(0));
            }
            return radio;

        } else if (exercise instanceof MultipleChoice multipleChoice) {
            CheckboxGroup<String> checkbox = new CheckboxGroup<>();
            checkbox.setItems(multipleChoice.getChoiceOptions().stream().map(ChoiceOption::getText).toList());
            if (saved != null && saved.getSelectedOptions() != null) {
                checkbox.setValue(new HashSet<>(saved.getSelectedOptions()));
            }
            return checkbox;

        } else if (exercise instanceof FreetextExercise) {
            TextArea textArea = new TextArea();
            textArea.setWidthFull();
            if (saved != null && saved.getTextAnswer() != null) {
                textArea.setValue(saved.getTextAnswer());
            }
            return textArea;

        } else if (exercise instanceof AssignmentExercise) {
            // TODO: Umsetzung der Zuordnungsaufgabe
            return new Paragraph("Zuordnungsaufgabe: (noch nicht implementiert)");
        }

        return new Paragraph("Unbekannter Aufgabentyp.");
    }

    private void saveAnswer() {
        if (currentIndex >= exercises.size()) return;

        Exercise exercise = exercises.get(currentIndex);
        Answer answer = new Answer();
        answer.setExercise(exercise);

        if (currentComponent instanceof RadioButtonGroup<?>) {
            String selected = ((RadioButtonGroup<String>) currentComponent).getValue();
            answer.setSelectedOptions(selected != null ? List.of(selected) : List.of());
        } else if (currentComponent instanceof CheckboxGroup<?>) {
            Set<String> selected = ((CheckboxGroup<String>) currentComponent).getValue();
            answer.setSelectedOptions(selected != null ? new ArrayList<>(selected) : new ArrayList<>());
        } else if (currentComponent instanceof TextArea) {
            String text = ((TextArea) currentComponent).getValue();
            answer.setTextAnswer(text != null ? text : "");
        }

        System.out.println("Speichere Antwort für Exercise " + exercise.getId() + ": " + answer.getSelectedOptions());

        userAnswers.put(exercise.getId(), answer);
    }

    private void showSummary() {
        removeAll();
        add(timer);
        add(new H3("Zusammenfassung"));

        for (Exercise ex : exercises) {
            Answer answer = userAnswers.get(ex.getId());
            add(new Paragraph("Frage: " + ex.getExerciseText()));

            String preview = "(keine Antwort)";
            if (answer != null) {
                if (ex instanceof SingleChoice) {
                    List<String> opts = answer.getSelectedOptions();
                    preview = opts != null && !opts.isEmpty() ? opts.get(0) : "(keine Antwort)";
                } else if (ex instanceof MultipleChoice) {
                    List<String> opts = answer.getSelectedOptions();
                    preview = opts != null && !opts.isEmpty() ? String.join(", ", opts) : "(keine Antwort)";
                } else if (ex instanceof FreetextExercise) {
                    String text = answer.getTextAnswer();
                    preview = text != null && !text.isBlank() ? text : "(keine Antwort)";
                } else if (ex instanceof AssignmentExercise) {
                    preview = "(Zuordnungsanzeige folgt)";
                }
            }

            add(new Paragraph("Antwort: " + preview));
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
        blockExamAppointment();
    }

    private void blockExamAppointment() {
        //PLATZHALTER
    }

    private void showError(String message) {
        removeAll();
        add(new Paragraph(message));
    }
}