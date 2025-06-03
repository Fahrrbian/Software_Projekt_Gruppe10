package com.gruppe10.exercisemanagement.ui.view;

import com.gruppe10.base.ui.Layout.MainLayout;
import com.gruppe10.examManagement.exam.domain.Exam;
import com.gruppe10.examManagement.exam.domain.ExamExercise;
import com.gruppe10.examManagement.exam.service.ExamService;
import com.gruppe10.examManagement.examAppointment.domain.ExamAppointment;
import com.gruppe10.examManagement.examAppointment.service.ExamAppointmentService;
import com.gruppe10.exercisemanagement.domain.*;
import com.gruppe10.exercisemanagement.service.ExerciseService;
import com.gruppe10.submission.service.SubmissionService;
import com.gruppe10.usermanagement.domain.User;
import com.gruppe10.usermanagement.service.UserService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.*;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Route(value = "exercise/:exerciseId", layout = MainLayout.class)
@RolesAllowed("INSTRUCTOR")
public class ExerciseDetailView extends VerticalLayout implements BeforeEnterObserver {

    private final ExerciseService exerciseService;
    private final ExamService examService;
    private final SubmissionService submissionService;
    private final UserService userService;

    private final H1 pageTitle = new H1("Aufgabe");
    private final ExamAppointmentService examAppointmentService;
    private Paragraph exerciseTextParagraph = new Paragraph();
    private Paragraph scoreParagraph = new Paragraph();

    private Exercise currentExercise;

    @Autowired
    public ExerciseDetailView(ExerciseService exerciseService, ExamService examService, SubmissionService submissionService, UserService userService, ExamAppointmentService examAppointmentService) {
        this.exerciseService = exerciseService;
        this.examService = examService;
        this.submissionService = submissionService;
        this.userService = userService;
        setPadding(true);
        setSpacing(true);
        setWidthFull();
        this.examAppointmentService = examAppointmentService;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        RouteParameters parameters = event.getRouteParameters();
        long exerciseId = Long.parseLong(parameters.get("exerciseId").orElse("-1"));

        Optional<Exercise> exercise = exerciseService.getById(exerciseId);

        if (exercise.isPresent()) {
            this.currentExercise = exercise.get();
            displayExerciseDetails(this.currentExercise);
        } else {
            removeAll();
            add(new H2("Aufgabe nicht gefunden"));
            add(new Paragraph("Die angeforderte Aufgabe konnte nicht gefunden werden."));
        }
    }

    private void displayExerciseDetails(Exercise exercise) {
        removeAll();

        add(pageTitle);
        exerciseTextParagraph.setText(exercise.getExerciseText());
        exerciseTextParagraph.getStyle().set("margin-top", "0.5em").set("margin-bottom", "1.5em");
        add(exerciseTextParagraph);

        VerticalLayout scoreSection = createSection("Punkte");
        scoreParagraph.setText(String.valueOf(exercise.getScore()));
        scoreParagraph.getStyle()
                .set("font-size", "1.1em")
                .set("margin", "0");
        scoreSection.add(scoreParagraph);
        add(scoreSection);

        VerticalLayout tagsSection = createSection("Tags");
        FlowLayout tagsFlowLayout = new FlowLayout();
        exercise.getTags().forEach(tag -> {
            Span tagChip = new Span(tag.getName());
            tagChip.getElement().getThemeList().add("badge pill");
            tagChip.getStyle()
                    .set("background-color", "var(--lumo-contrast-5pct)")
                    .set("padding", "0.4em 0.8em")
                    .set("border-radius", "1em")
                    .set("font-size", "0.9em");
            tagsFlowLayout.add(tagChip);
        });
        tagsSection.add(tagsFlowLayout);
        add(tagsSection);

        VerticalLayout exerciseTypeSection = createSection("Aufgabentyp");
        Paragraph typeDescription = new Paragraph();
        typeDescription.getStyle().set("margin", "0");
        exerciseTypeSection.add(typeDescription);
        add(exerciseTypeSection);

        if (exercise instanceof SingleChoice singleChoice) {
            typeDescription.setText("Single Choice");
            VerticalLayout choiceOptionsSection = createSection("Auswahlmöglichkeiten");
            showChoiceOptions(choiceOptionsSection, singleChoice.getChoiceOptions().stream().toList());
            add(choiceOptionsSection);
        } else if (exercise instanceof MultipleChoice multipleChoice) {
            typeDescription.setText("Multiple Choice");
            VerticalLayout choiceOptionsSection = createSection("Auswahlmöglichkeiten");
            showChoiceOptions(choiceOptionsSection, multipleChoice.getChoiceOptions().stream().toList());
            add(choiceOptionsSection);
        } else if (exercise instanceof AssignmentExercise assignment) {
            typeDescription.setText("Zuordnungsaufgabe");
            VerticalLayout assignmentPairsSection = createSection("Zuordnungspaare");
            showAssignmentPairs(assignmentPairsSection, assignment.getAssignmentPairs().stream().toList());
            add(assignmentPairsSection);
        } else {
            typeDescription.setText(exercise.getClass().getSimpleName());
        }

        Button addExamButton = new Button("Zu Prüfung hinzufügen");
        addExamButton.addClickListener(e -> openAddExamDialog());
        addExamButton.getStyle().set("margin-top", "1.5em");
        add(addExamButton);
    }

    private VerticalLayout createSection(String titleText) {
        VerticalLayout sectionLayout = new VerticalLayout();
        sectionLayout.setPadding(true);
        sectionLayout.setSpacing(false);
        sectionLayout.setWidthFull();
        sectionLayout.getStyle()
                .set("background-color", "var(--lumo-base-color)")
                .set("border", "1px solid var(--lumo-contrast-10pct)")
                .set("border-radius", "var(--lumo-border-radius-m)")
                .set("margin-bottom", "1.5em");

        H3 sectionTitle = new H3(titleText);
        sectionTitle.getStyle().set("margin-top", "0").set("margin-bottom", "0.8em");
        sectionLayout.add(sectionTitle);
        return sectionLayout;
    }

    private void showChoiceOptions(VerticalLayout parentSectionLayout, List<ChoiceOption> options) {
        for (ChoiceOption option : options) {
            HorizontalLayout optionLayout = new HorizontalLayout();
            optionLayout.setSpacing(false);
            optionLayout.getStyle().set("width", "100%");
            optionLayout.getStyle().set("margin-bottom", "0.8em");

            Icon statusIcon;
            if (option.isCorrect()) {
                statusIcon = VaadinIcon.CHECK.create();
                statusIcon.setColor("var(--lumo-success-text-color)");
            } else {
                statusIcon = VaadinIcon.CLOSE.create();
                statusIcon.setColor("var(--lumo-error-text-color)");
            }
            statusIcon.getStyle()
                    .set("margin-right", "0.5em")
                    .set("margin-top", "5px")
                    .set("flex-shrink", "0");
            statusIcon.setSize("1.1em");

            Paragraph optionText = new Paragraph(option.getText());
            optionText.getStyle()
                    .set("margin", "0")
                    .set("flex-grow", "1")
                    .set("white-space", "normal");

            optionLayout.add(statusIcon, optionText);
            parentSectionLayout.add(optionLayout);
        }
    }

    private void showAssignmentPairs(VerticalLayout parentSectionLayout, List<AssignmentPair> pairs) {
        for (AssignmentPair pair : pairs) {
            HorizontalLayout pairLayout = new HorizontalLayout();
            pairLayout.setAlignItems(FlexComponent.Alignment.CENTER);
            pairLayout.setSpacing(false);
            pairLayout.getStyle().set("width", "100%");
            pairLayout.getStyle().set("display", "grid");
            pairLayout.getStyle().set("grid-template-columns", "1fr auto 1fr");
            pairLayout.getStyle().set("align-items", "center");

            Paragraph partOne = new Paragraph(pair.getPartOne());
            partOne.getStyle()
                    .set("margin", "0")
                    .set("text-align", "left")
                    .set("padding-right", "0.5em")
                    .set("white-space", "normal");

            Icon arrowIcon = VaadinIcon.ARROW_RIGHT.create();
            arrowIcon.getStyle()
                    .set("margin", "0 0.8em")
                    .set("flex-shrink", "0")
                    .set("text-align", "center");
            arrowIcon.setSize("1.2em");

            Paragraph partTwo = new Paragraph(pair.getPartTwo());
            partTwo.getStyle()
                    .set("margin", "0")
                    .set("text-align", "left")
                    .set("padding-left", "0.5em")
                    .set("white-space", "normal");

            pairLayout.add(partOne, arrowIcon, partTwo);
            parentSectionLayout.add(pairLayout);

            Div separator = new Div();
            separator.getStyle()
                    .set("width", "100%")
                    .set("height", "1px")
                    .set("background-color", "var(--lumo-contrast-5pct)")
                    .set("margin-top", "0.8em")
                    .set("margin-bottom", "0.8em");
            parentSectionLayout.add(separator);
        }

        if (!pairs.isEmpty() && parentSectionLayout.getComponentCount() > 0) {
            Component lastComponent = parentSectionLayout.getComponentAt(parentSectionLayout.getComponentCount() - 1);
            if (lastComponent instanceof Div &&
                    lastComponent.getStyle().has("background-color") &&
                    lastComponent.getStyle().get("background-color").equals("var(--lumo-contrast-5pct)")) {
                parentSectionLayout.remove(lastComponent);
            }
        }
    }

    private void openAddExamDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Aufgabe zu Prüfung hinzufügen");

        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.setSpacing(true);
        dialogLayout.setPadding(true);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        Long currentUserId = null;
        if (principal instanceof UserDetails userDetails) {
            String username = userDetails.getUsername();
            Optional<User> optionalUser = userService.findByEmail(username);

            if (optionalUser.isPresent()) {
                currentUserId = optionalUser.get().getId();
            } else {
                Notification.show("Fehler: Aktueller Benutzer konnte nicht in der Datenbank gefunden werden.", 3000, Notification.Position.MIDDLE);
                return;
            }

            boolean isInstructor = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_INSTRUCTOR"));
            if (!isInstructor) {
                Notification.show("Fehler: Sie haben nicht die erforderliche Berechtigung (Instructor).", 3000, Notification.Position.MIDDLE);
                return;
            }

        } else {
            Notification.show("Fehler: Aktueller Benutzer konnte nicht ermittelt werden.", 3000, Notification.Position.MIDDLE);
            return;
        }

        final Long finalCurrentUserId = currentUserId;

        List<ExamAppointment> exams = examAppointmentService.getAllExams().stream()
                .filter(exam -> exam.getCreator() != null && exam.getCreator().getId().equals(finalCurrentUserId))
                .filter(exam -> !submissionService.existsByExam(exam))
                .collect(Collectors.toList());

        if (exams.isEmpty()) {
            Notification.show("Es gibt keine verfügbaren Prüfungen, denen diese Aufgabe hinzugefügt werden kann (entweder keine eigenen Prüfungen oder alle bereits geschrieben).", 3000, Notification.Position.MIDDLE);
        } else {
            Grid<Exam> examGrid = new Grid<>(Exam.class, false);
            examGrid.setItems(exams);
            examGrid.addColumn(Exam::getTitle).setHeader("Prüfungstitel");
            examGrid.addColumn(exam -> exam.getCreationDate().toString()).setHeader("Erstellungsdatum");
            examGrid.setSelectionMode(Grid.SelectionMode.MULTI);

            dialogLayout.add(new Paragraph("Wählen Sie die Prüfungen aus, zu denen diese Aufgabe hinzugefügt werden soll:"));
            dialogLayout.add(examGrid);

            Button addButton = new Button("Aufgabe hinzufügen");
            addButton.addClickListener(e -> {
                Set<Exam> selectedExams = examGrid.getSelectedItems();
                if (!selectedExams.isEmpty()) {
                    addExerciseToExams(selectedExams, currentExercise);
                    dialog.close();
                } else {
                    Notification.show("Bitte wählen Sie mindestens eine Prüfung aus.", 3000, Notification.Position.MIDDLE);

                }
            });
            dialogLayout.add(addButton);
        }

        Button closeButton = new Button("Abbrechen", e -> dialog.close());
        dialogLayout.add(closeButton);

        dialog.add(dialogLayout);
        dialog.open();
    }

    private void addExerciseToExams(Set<Exam> exams, Exercise exerciseToAdd) {
        for (Exam exam : exams) {
            boolean alreadyExists = exam.getExamExercises().stream()
                    .anyMatch(ee -> ee.getExercise().getId().equals(exerciseToAdd.getId()));

            if (!alreadyExists) {
                ExamExercise examExercise = new ExamExercise();
                examExercise.setExam(exam);
                examExercise.setExercise(exerciseToAdd);
                examExercise.setPosition(exam.getExamExercises().size());
                exam.getExamExercises().add(examExercise);
                examService.saveExam(exam);
                Notification.show("Aufgabe " + exerciseToAdd.getExerciseText() + " zu Prüfung " + exam.getTitle() + " hinzugefügt.", 3000, Notification.Position.MIDDLE);
            } else {
                Notification.show("Aufgabe " + exerciseToAdd.getExerciseText() + " ist bereits in Prüfung " + exam.getTitle() + ".", 3000, Notification.Position.MIDDLE);
            }
        }
    }

    private static class FlowLayout extends Div {
        public FlowLayout() {
            getStyle().set("display", "flex");
            getStyle().set("flex-wrap", "wrap");
            getStyle().set("gap", "0.5em");
        }
    }
}