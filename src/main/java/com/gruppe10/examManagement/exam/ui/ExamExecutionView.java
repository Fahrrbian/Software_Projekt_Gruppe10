/**
 * Author: Christian Markow
 * Date: 27.05.2025
 */

package com.gruppe10.examManagement.exam.ui;

import com.gruppe10.base.ui.Layout.MainLayout;
import com.gruppe10.examManagement.exam.domain.Exam;
import com.gruppe10.examManagement.exam.domain.ExamRepository;
import com.gruppe10.examManagement.exam.service.ExamService;
import com.gruppe10.examManagement.examAppointment.domain.StudentExam;
import com.gruppe10.examManagement.examAppointment.domain.StudentExamRepository;
import com.gruppe10.exercisemanagement.domain.*;
import com.gruppe10.exercisemanagement.service.ExerciseService;
import com.gruppe10.submission.domain.Submission;
import com.gruppe10.submission.domain.SubmissionAnswer;
import com.gruppe10.submission.service.SubmissionService;
import com.gruppe10.timer.Timer;
import com.gruppe10.usermanagement.domain.Student;
import com.gruppe10.usermanagement.domain.User;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.combobox.ComboBox;
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

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static com.gruppe10.base.ui.security.SecurityUtils.getCurrentUser;

@Route(value = "exam/:examId", layout = MainLayout.class)
@PageTitle("Prüfung")
@RolesAllowed({"INSTRUCTOR", "STUDENT"})
public class ExamExecutionView extends VerticalLayout implements BeforeEnterObserver {

    private final ExerciseService exerciseService;
    private final ExamService examService;
    private final SubmissionService submissionService;
    private final ExamRepository examRepository;
    private final StudentExamRepository studentExamRepository;
    private Exam exam;
    private StudentExam studentExam;

    private List<Exercise> exercises = new ArrayList<>();
    private Map<Long, Answer> userAnswers = new HashMap<>();
    private final Map<Long, Map<String, ComboBox<String>>> assignmentComboBoxMap = new HashMap<>();


    private int currentIndex = 0;
    private Component currentComponent;
    private Timer timer;

    @Autowired
    public ExamExecutionView(ExerciseService exerciseService, ExamService examService, SubmissionService submissionService, ExamRepository examRepository, StudentExamRepository studentExamRepository) {
        this.exerciseService = exerciseService;
        this.examService = examService;
        this.submissionService = submissionService;
        this.examRepository = examRepository;
        this.studentExamRepository = studentExamRepository;
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
        Optional<User> currentUser = getCurrentUser();
        if (currentUser.isEmpty()) {
            showError("Benutzer nicht gefunden.");
            return;
        }

        Student student = (Student) currentUser.get();

        System.out.println("Student-ID: " + student.getId());
        System.out.println("Exam-ID: " + examId);

        Optional<StudentExam> optionalExam = studentExamRepository.findByStudent_IdAndExam_Id(student.getId(), examId);
        if (optionalExam.isEmpty()) {
            // Prüfung wurde noch nicht gestartet – neuen StudentExam-Eintrag anlegen
            Optional<Exam> examOpt = examRepository.findById(examId);
            if (examOpt.isEmpty()) {
                showError("Prüfung nicht vorhanden.");
                return;
            }

            Exam exam = examOpt.get();

            StudentExam newStudentExam = new StudentExam();
            newStudentExam.setExam(exam);
            newStudentExam.setStudent(student);
            newStudentExam.setVorname(student.getForename());
            newStudentExam.setNachname(student.getSurname());
            newStudentExam.setGesperrt(false);
            newStudentExam.setCompleted(false);

            studentExam = studentExamRepository.save(newStudentExam);
        } else {
            studentExam = optionalExam.get();

            if (studentExam.isGesperrt()) {
                showError("Diese Prüfung ist gesperrt und kann nicht mehr bearbeitet werden.");
                return;
            }
        }

        exercises = exerciseService.getAllByExamId(examId);

        timer = new Timer(30 * 60 * 1000L, this::onTimeUp); //30 Minuten
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

        } else if (exercise instanceof FreetextExercise freetextExercise) {
            TextArea textArea = new TextArea();
            textArea.setWidthFull();
            if (saved != null && saved.getTextAnswer() != null) {
                textArea.setValue(saved.getTextAnswer());
            }
            return textArea;

        } else if (exercise instanceof AssignmentExercise assignmentExercise) {
            Map<String, ComboBox<String>> comboBoxes = new HashMap<>();
            VerticalLayout layout = new VerticalLayout();

            List<String> options = assignmentExercise.getAssignmentPairs().stream()
                    .map(AssignmentPair::getPartTwo)
                    .collect(Collectors.toList());

            Collections.shuffle(options);

            for (AssignmentPair pair : assignmentExercise.getAssignmentPairs()) {
                String left = pair.getPartOne();
                ComboBox<String> comboBox = new ComboBox<>();
                comboBox.setLabel(left);
                comboBox.setItems(options);
                comboBox.setWidth("40%");

                if (saved != null && saved.getAssignmentMappings() != null) {
                    comboBox.setValue(saved.getAssignmentMappings().get(left));
                }

                layout.add(comboBox);
                comboBoxes.put(left, comboBox);
            }

            assignmentComboBoxMap.put(exercise.getId(), comboBoxes);

            return layout;

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
        } else if (exercise instanceof AssignmentExercise) {
            Map<String, ComboBox<String>> comboBoxes = assignmentComboBoxMap.get(exercise.getId());
            if (comboBoxes != null) {
                Map<String, String> mappings = new HashMap<>();
                for (Map.Entry<String, ComboBox<String>> entry : comboBoxes.entrySet()) {
                    mappings.put(entry.getKey(), entry.getValue().getValue());
                }
                answer.setAssignmentMappings(mappings);
            }
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
                    Map<String, String> mappings = answer.getAssignmentMappings();
                    if (mappings != null && !mappings.isEmpty()) {
                        StringBuilder sb = new StringBuilder();
                        for (Map.Entry<String, String> entry : mappings.entrySet()) {
                            String key = entry.getKey();
                            String value = entry.getValue() != null ? entry.getValue() : "X";
                            sb.append(key).append(" ➝ ").append(value).append(";\n");
                        }
                        preview = !sb.isEmpty() ? sb.toString() : "(keine Antwort)";
                    }
                }
            }

            add(new Paragraph("Antwort: " + preview));
            add(new Hr());
        }

        Button submitButton = new Button("Prüfung abschließen", e -> submitExam());
        add(submitButton);
    }

    private void onTimeUp() {
        Notification.show("Zeit abgelaufen. Prüfung wird abgegeben.");
        submitExam();
    }

    private void submitExam() {
        Submission submission = new Submission();

        submission.setExam(studentExam.getExam());
        submission.setSubmittedAt(Instant.now());
        submission.setStudent(studentExam.getStudent());

        List<SubmissionAnswer> submissionAnswers = new ArrayList<>();
        for (Map.Entry<Long, Answer> entry : userAnswers.entrySet()) {
            Answer userAnswer = entry.getValue();

            SubmissionAnswer submissionAnswer = new SubmissionAnswer();
            submissionAnswer.setSubmission(submission);

            submissionAnswer.setQuestionId(String.valueOf(userAnswer.getExercise().getId()));

            submissionAnswer.setAnswerData(userAnswer.getTextAnswer());

            submissionAnswers.add(submissionAnswer);
        }

        submission.setAnswers(submissionAnswers);

        studentExam.setSubmission(submission);
        studentExam.setCompleted(true);
        studentExam.setGesperrt(true);

        studentExamRepository.save(studentExam);
        Notification.show("Prüfung abgegeben.");
        UI.getCurrent().navigate("user-info");
        //blockExamAppointment();
    }

    /*
    private void blockExamAppointment() {
        if (studentExam != null) {
            studentExam.setGesperrt(true);
            studentExam.setCompleted(true);
            studentExamRepository.save(studentExam);
        }
    }
     */

    private void showError(String message) {
        removeAll();
        add(new Paragraph(message));
    }

}