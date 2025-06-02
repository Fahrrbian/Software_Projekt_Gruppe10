/**
 * Author: Christian Markow
 * Date: 27.05.2025
 */

package com.gruppe10.examManagement.exam.ui;

import com.gruppe10.base.ui.Layout.MainLayout;
import com.gruppe10.base.ui.view.MainView;
import com.gruppe10.examManagement.exam.domain.Exam;
import com.gruppe10.examManagement.exam.domain.ExamExercise;
import com.gruppe10.examManagement.exam.service.ExamService;
import com.gruppe10.exercisemanagement.domain.*;
import com.gruppe10.exercisemanagement.service.ExerciseService;
import com.gruppe10.submission.domain.FreeTextAnswer;
import com.gruppe10.submission.domain.MultipleChoiceAnswer;
import com.gruppe10.submission.domain.SingleChoiceAnswer;
import com.gruppe10.submission.domain.Submission;
import com.gruppe10.submission.service.EvaluationService;
import com.gruppe10.submission.service.SubmissionService;
import com.gruppe10.timer.Timer;
import com.gruppe10.usermanagement.domain.Student;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.charts.model.Label;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
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

    private final ExamService examService;
    private final EvaluationService evaluationService;
    private final SubmissionService submissionService;

    private Optional<Exam> currentExam;

    private final Map<String, String> rawAnswers = new HashMap<>();
    private final Map<String, Exercise> exerciseMap = new HashMap<>();

    private Timer timer;
    private Component currentComponent;

    @Autowired
    public ExamExecutionView(ExamService examService, EvaluationService evaluationService, SubmissionService submissionService) {
        this.examService = examService;
        this.evaluationService = evaluationService;
        this.submissionService = submissionService;
        setSizeFull();
        setPadding(true);
    }

    public void beforeEnter(BeforeEnterEvent event) {
        String examId = event.getRouteParameters().get("examId").orElse(null);
        if (examId != null) {
            Long id = Long.parseLong(examId);
            this.currentExam = examService.getExamWithExercises(id);
            if (this.currentExam.isEmpty()) {
                event.forwardTo(MainView.class);
            } else {
                initUI(currentExam);
            }
        } else {
            add(new H3("Fehler: Keine Prüfung-ID angegeben."));
        }
    }

    private void initUI(Optional<Exam> exam) {
        removeAll();
        System.out.println("Exam geladen: " + exam);
        add(new H2("Prüfung: " + exam.get().getTitle()));

        exam.get().getExamExercises().stream()
                .sorted(Comparator.comparing(ExamExercise::getPosition))
                .forEach(ee -> {
                    Exercise ex = ee.getExercise();
                    String questionId = ex.getId().toString();
                    exerciseMap.put(questionId, ex);

                    add(new Hr(), new Paragraph("Frage: " + ee.getPosition() + ":"));

                    if (ex instanceof SingleChoice singleChoice) {
                        RadioButtonGroup<ChoiceOption> radioButtonGroup = new RadioButtonGroup<>();
                        radioButtonGroup.setLabel(singleChoice.getExerciseText());
                        radioButtonGroup.setItems(singleChoice.getChoiceOptions());
                        radioButtonGroup.setItemLabelGenerator(ChoiceOption::getText);
                        radioButtonGroup.addValueChangeListener(e -> {
                            if (e.getValue() != null) {
                                rawAnswers.put(questionId, e.getValue().getId().toString());
                            }
                        });
                        add(radioButtonGroup);
                    } else if (ex instanceof MultipleChoice multipleChoice) {
                        CheckboxGroup<ChoiceOption> checkboxGroup = new CheckboxGroup<>();
                        checkboxGroup.setLabel(multipleChoice.getExerciseText());
                        checkboxGroup.setItems(multipleChoice.getChoiceOptions());
                        checkboxGroup.setItemLabelGenerator(ChoiceOption::getText);
                        checkboxGroup.addValueChangeListener(e -> {
                            List<String> ids = e.getValue().stream()
                                    .map(option -> {
                                        return option.getId().toString();
                                    })
                                    .toList();
                            rawAnswers.put(questionId, String.join(",", ids));
                        });
                        add(checkboxGroup);
                    } else if (ex instanceof FreetextExercise freetextExercise) {
                        TextArea text = new TextArea(freetextExercise.getExerciseText());
                        text.setWidthFull();
                        text.addValueChangeListener(e -> {
                            rawAnswers.put(questionId, e.getValue());
                        });
                        add(text);
                    } else if (ex instanceof AssignmentExercise assignmentExercise) {
                        //PLATZHALTER
                        Label info = new Label("Zuordnungsaufgabe: (noch nicht implementiert)");
                        add(String.valueOf(info));
                    }
                });
        Button submitBtn = new Button("Prüfung abschließen", e -> {
            handleSubmission();
        });
        submitBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        add(new Hr(), submitBtn);
    }

    private void handleSubmission() {
        Student student = new Student();
        student.setEmail(student.getEmail());
        student.setForename(student.getForename());
        student.setSurname(student.getSurname());

        Map<String, com.gruppe10.submission.domain.Answer> studentAnswers = new HashMap<>();
        for (Map.Entry<String, String> entry : rawAnswers.entrySet()) {
            String questionId = entry.getKey();
            String raw = entry.getValue();
            Exercise exercise = exerciseMap.get(questionId);
            studentAnswers.put(questionId, convertToAnswer(exercise, questionId, raw));
        }

        var result = evaluationService.evaluateExam(currentExam.orElse(null), studentAnswers);

        Submission savedAnswers = submissionService.bewerten(
                currentExam.orElse(null),
                student,
                result.getPerQuestionPoints(),
                rawAnswers
        );

        Notification.show("Gespeichert: " + result.getTotalPoints() + " Punkte - " + (result.isPassed() ? "Bestanden" : "Nicht bestanden"));
        UI.getCurrent().navigate("user-info");
    }

    private com.gruppe10.submission.domain.Answer convertToAnswer(Exercise exercise, String questionId, String raw) {
        if (exercise instanceof SingleChoice) {
            var sca = new SingleChoiceAnswer();
            sca.setQuestionId(questionId);
            sca.setSelectedOptionId(raw);
            return sca;
        } else if (exercise instanceof MultipleChoice) {
            var mca = new MultipleChoiceAnswer();
            mca.setQuestionId(questionId);
            Set<String> selectedIds = new HashSet<>(Arrays.asList(raw.split(",")));
            mca.setSelectedOptionIds((List<String>) selectedIds);
            return mca;
        } else if (exercise instanceof FreetextExercise) {
            var fta = new FreeTextAnswer();
            fta.setQuestionId(questionId);
            fta.setText(raw);
            return fta;
        } else if (exercise instanceof AssignmentExercise) {
            // TODO: AssignementExercise implementieren
            return null;
        }
        return null;
    }
}

    /*

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        String examIdParam = event.getRouteParameters().get("pruefung_id").orElse(null);
        if (examIdParam != null) {
            loadExam(Long.parseLong(examIdParam));
        } else {
            add(new Paragraph("Fehler: Prüfung nicht gefunden."));
        }
    }

    private void loadExam(Long examId) {
        exercises = exerciseService.getAllByExamId(examId);

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

        currentComponent = createInputComponent(exercise);
        add(currentComponent);

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
        add(navButtons);
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
            TextArea text = new TextArea();
            text.setWidthFull();

            if (saved != null && saved.getTextAnswer() != null) {
                text.setValue(saved.getTextAnswer());
            }

            return text;

        } else if (exercise instanceof AssignmentExercise) {
            // TODO: UI für Zuordnungsaufgabe
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
            answer.setSelectedOptions(new ArrayList<>(selected));

        } else if (currentComponent instanceof TextArea) {
            String text = ((TextArea) currentComponent).getValue();
            answer.setTextAnswer(text);
        }

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
                if (ex instanceof SingleChoice || ex instanceof MultipleChoice) {
                    preview = String.join(", ", answer.getSelectedOptions());
                } else if (ex instanceof FreetextExercise) {
                    preview = answer.getTextAnswer();
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
    }
}

     */
