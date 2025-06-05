package com.gruppe10.exercisemanagement.ui.view;

import com.gruppe10.base.ui.Layout.MainLayout;
import com.gruppe10.examManagement.exam.domain.Exam;
import com.gruppe10.examManagement.exam.service.ExamService;
import com.gruppe10.exercisemanagement.domain.*;
import com.gruppe10.exercisemanagement.service.*;
import com.gruppe10.submission.service.SubmissionService;
import com.gruppe10.usermanagement.domain.User;
import com.gruppe10.usermanagement.service.UserService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.*;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.*;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.stream.Collectors;

@Route(value = "exercise/:exerciseId", layout = MainLayout.class)
@RolesAllowed("INSTRUCTOR")
public class ExerciseDetailView extends VerticalLayout implements BeforeEnterObserver {

    private final ExerciseService exerciseService;
    private final ExamService examService;
    private final SubmissionService submissionService;
    private final UserService userService;
    private final TagService tagService;

    private final SingleChoiceService singleChoiceService;
    private final MultipleChoiceService multipleChoiceService;
    private final AssignmentExerciseService assignmentExerciseService;
    private final FreetextExerciseService freeTextExerciseService;

    private final H1 pageTitle = new H1("Aufgabe");
    private Paragraph exerciseTextParagraph = new Paragraph();
    private TextArea exerciseTextEditor = new TextArea();
    private Paragraph scoreParagraph = new Paragraph();
    private NumberField scoreEditor = new NumberField();

    private HorizontalLayout buttonLayout = new HorizontalLayout();

    private Exercise currentExercise;
    private boolean isEditing = false;
    private boolean hasUnsavedChanges = false;

    private MultiSelectComboBox<Tag> tagSelector = new MultiSelectComboBox<>("Tags");
    private Set<Tag> selectedTags = new HashSet<>();

    private VerticalLayout specificContentContainer  = new VerticalLayout();
    private Button addOptionOrPairButton = new Button("+ Antwortmöglichkeit hinzufügen");

    private List<ChoiceOptionEditor> choiceOptionEditors = new ArrayList<>();
    private List<AssignmentPairEditor> assignmentPairEditors = new ArrayList<>();

    private Binder<Exercise> mainBinder = new Binder<>();

    private com.vaadin.flow.shared.Registration addOptionOrPairButtonClickListenerRegistration;

    @Autowired
    public ExerciseDetailView(ExerciseService exerciseService, ExamService examService, SubmissionService submissionService, UserService userService, AssignmentExerciseService assignmentExerciseService, SingleChoiceService singleChoiceService, MultipleChoiceService multipleChoiceService, FreetextExerciseService freeTextExerciseService, TagService tagService) {
        this.exerciseService = exerciseService;
        this.examService = examService;
        this.submissionService = submissionService;
        this.userService = userService;
        this.singleChoiceService = singleChoiceService;
        this.multipleChoiceService = multipleChoiceService;
        this.assignmentExerciseService = assignmentExerciseService;
        this.freeTextExerciseService = freeTextExerciseService;
        this.tagService = tagService;
        setPadding(true);
        setSpacing(true);
        setWidthFull();

        exerciseTextEditor.setWidthFull();
        exerciseTextEditor.setPlaceholder("Geben Sie die Aufgabenstellung ein...");
        exerciseTextEditor.addValueChangeListener(e -> setUnsavedChanges(true));

        scoreEditor.setPlaceholder("Punkte");
        scoreEditor.setMin(0);
        scoreEditor.setStep(1);
        scoreEditor.addValueChangeListener(e -> setUnsavedChanges(true));

        tagSelector.setItems(tagService.getAll());
        tagSelector.setWidthFull();
        tagSelector.setItemLabelGenerator(Tag::getName);
        tagSelector.setClearButtonVisible(true);
        tagSelector.setAllowCustomValue(true);
        tagSelector.setPlaceholder("Tags auswählen oder neu eingeben...");
        tagSelector.setTooltipText("Um neue Tags hinzuzufügen, geben Sie den Namen ein und drücken Sie Enter. Bestehende Tags können Sie auswählen.");
        tagSelector.setRenderer(new ComponentRenderer<>(tag -> {
            Span tagSpan = new Span(tag.getName());
            tagSpan.getElement().getThemeList().add("badge pill");
            return tagSpan;
        }));
        tagSelector.addCustomValueSetListener(event -> {
            String newTagName = event.getDetail().trim();
            if (!newTagName.isEmpty()) {
                Tag newTag = tagService.findOrCreateByName(newTagName);
                if (newTag != null) {
                    tagSelector.setItems(tagService.getAll());
                    selectedTags.add(newTag);
                    tagSelector.select(selectedTags);
                    Notification.show("Tag '" + newTagName + "' hinzugefügt.", 3000, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_SUCCESS);;
                    setUnsavedChanges(true);
                } else {
                    Notification.show("Fehler beim Hinzufügen des Tags: " + newTagName, 3000, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_ERROR);;
                }
            }
        });
        tagSelector.addValueChangeListener(event -> {
            if (event.getValue() != null) {
                selectedTags.addAll(event.getValue());
            }
            setUnsavedChanges(true);
        });

        mainBinder.forField(exerciseTextEditor)
                .asRequired("Aufgabentext darf nicht leer sein")
                .bind(Exercise::getExerciseText, Exercise::setExerciseText);

        mainBinder.forField(scoreEditor)
                .asRequired("Punkte eingeben")
                .withConverter(Double::intValue, Integer::doubleValue, "Bitte eine gültige Zahl eingeben")
                .withValidator(score -> score != null && score > 0, "Punktzahl muss größer 0 sein")
                .bind(Exercise::getScore, Exercise::setScore);
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

    public void beforeLeave(BeforeLeaveEvent event) {
        if (hasUnsavedChanges && isEditing) {
            Dialog confirmDialog = new Dialog();
            confirmDialog.setHeaderTitle("Ungespeicherte Änderungen");
            confirmDialog.add("Sie haben ungespeicherte Änderungen. Möchten Sie die Seite wirklich verlassen?");

            Button discardButton = new Button("Verlassen");
            discardButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
            discardButton.addClickListener(e -> {
                confirmDialog.close();
                setUnsavedChanges(false);
                event.getContinueNavigationAction().proceed();
            });
            Button cancelButton = new Button("Abbrechen");
            cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            cancelButton.addClickListener(e -> confirmDialog.close());

            confirmDialog.getFooter().add(discardButton, cancelButton);
            confirmDialog.open();
            event.postpone();
        }
    }

    private void setUnsavedChanges(boolean hasChanges) {
        this.hasUnsavedChanges = hasChanges;
    }

    private void displayExerciseDetails(Exercise exercise) {
        removeAll();
        isEditing = false;
        setUnsavedChanges(false);

        choiceOptionEditors.clear();
        assignmentPairEditors.clear();
        specificContentContainer.removeAll();

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
        if (exercise.getTags() != null) {
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
        } else {
            tagsFlowLayout.add(new Span("Keine Tags vorhanden."));
        }
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

        buttonLayout.removeAll();
        Button editButton = new Button("Bearbeiten", VaadinIcon.EDIT.create());
        editButton.addClickListener(e -> enterEditMode());
        buttonLayout.add(editButton);
        add(buttonLayout);

        Button addExamButton = new Button("Zu Prüfung hinzufügen");
        addExamButton.addClickListener(e -> openAddExamDialog());
        addExamButton.getStyle().set("margin-top", "1.5em");
        add(addExamButton);
    }

    private void enterEditMode() {
        removeAll();
        isEditing = true;
        setUnsavedChanges(false);

        choiceOptionEditors.clear();
        assignmentPairEditors.clear();
        specificContentContainer.removeAll();

        add(pageTitle);

        exerciseTextEditor.setValue(currentExercise.getExerciseText());
        exerciseTextEditor.getStyle().set("margin-top", "0.5em").set("margin-bottom", "1.5em");
        add(exerciseTextEditor);

        VerticalLayout scoreSection = createSection("Punkte");
        scoreEditor.setValue((double) currentExercise.getScore());
        scoreEditor.getStyle().set("margin", "0");
        scoreSection.add(scoreEditor);
        add(scoreSection);

        VerticalLayout tagsSection = createSection("Tags");
        tagSelector.setItems(tagService.getAll());
        selectedTags.clear();
        if (currentExercise.getTags() != null) {
            selectedTags.addAll(currentExercise.getTags());
            tagSelector.setValue(selectedTags);
        } else {
            tagSelector.clear();
        }
        tagsSection.add(tagSelector);
        add(tagsSection);


        VerticalLayout exerciseTypeSection = createSection("Aufgabentyp");
        Paragraph typeDescription = new Paragraph();
        typeDescription.getStyle().set("margin", "0");
        exerciseTypeSection.add(typeDescription);
        add(exerciseTypeSection);

        if (addOptionOrPairButtonClickListenerRegistration != null) {
            addOptionOrPairButtonClickListenerRegistration.remove();
            addOptionOrPairButtonClickListenerRegistration = null;
        }

        if (currentExercise instanceof SingleChoice singleChoice) {
            typeDescription.setText("Single Choice");
            VerticalLayout choiceOptionsSection = createSection("Auswahlmöglichkeiten");
            if (singleChoice.getChoiceOptions() != null && !singleChoice.getChoiceOptions().isEmpty()) {
                singleChoice.getChoiceOptions().forEach(this::addChoiceOptionEditor);
            }
            if (choiceOptionEditors.isEmpty()) {
                ChoiceOption newOption = new ChoiceOption();
                newOption.setText("");
                newOption.setCorrect(false);
                addChoiceOptionEditor(newOption);
            }

            addOptionOrPairButton.setText("+ Antwortmöglichkeit hinzufügen");
            addOptionOrPairButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            addOptionOrPairButtonClickListenerRegistration = addOptionOrPairButton.addClickListener(e -> {
                ChoiceOption newOption = new ChoiceOption();
                newOption.setText("");
                newOption.setCorrect(false);
                addChoiceOptionEditor(newOption);
                setUnsavedChanges(true);
            });
            choiceOptionsSection.add(specificContentContainer, addOptionOrPairButton);
            add(choiceOptionsSection);

        } else if (currentExercise instanceof MultipleChoice multipleChoice) {
            typeDescription.setText("Multiple Choice");
            VerticalLayout choiceOptionsSection = createSection("Auswahlmöglichkeiten");
            if (multipleChoice.getChoiceOptions() != null && !multipleChoice.getChoiceOptions().isEmpty()) {
                multipleChoice.getChoiceOptions().forEach(this::addChoiceOptionEditor);
            }
            if (choiceOptionEditors.isEmpty()) {
                ChoiceOption newOption = new ChoiceOption();
                newOption.setText("");
                newOption.setCorrect(false);
                addChoiceOptionEditor(newOption);
            }

            addOptionOrPairButton.setText("+ Antwortmöglichkeit hinzufügen");
            addOptionOrPairButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            addOptionOrPairButtonClickListenerRegistration = addOptionOrPairButton.addClickListener(e -> {
                ChoiceOption newOption = new ChoiceOption();
                newOption.setText("");
                newOption.setCorrect(false);
                addChoiceOptionEditor(newOption);
                setUnsavedChanges(true);
            });
            choiceOptionsSection.add(specificContentContainer, addOptionOrPairButton);
            add(choiceOptionsSection);

        } else if (currentExercise instanceof AssignmentExercise assignment) {
            typeDescription.setText("Zuordnungsaufgabe");
            VerticalLayout assignmentPairsSection = createSection("Zuordnungspaare");
            if (assignment.getAssignmentPairs() != null && !assignment.getAssignmentPairs().isEmpty()) {
                assignment.getAssignmentPairs().forEach(this::addAssignmentPairEditor);
            }
            if (assignmentPairEditors.isEmpty()) {
                addAssignmentPairEditor(new AssignmentPair("", ""));
            }

            addOptionOrPairButton.setText("+ Zuordnungspaar hinzufügen");
            addOptionOrPairButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            addOptionOrPairButtonClickListenerRegistration = addOptionOrPairButton.addClickListener(e -> {
                addAssignmentPairEditor(new AssignmentPair("", ""));
                setUnsavedChanges(true);
            });
            assignmentPairsSection.add(specificContentContainer, addOptionOrPairButton);
            add(assignmentPairsSection);
        } else if (currentExercise instanceof FreetextExercise) {
            typeDescription.setText("Freitextaufgabe");
            specificContentContainer.removeAll();
            addOptionOrPairButton.setVisible(false);
        } else {
            typeDescription.setText(currentExercise.getClass().getSimpleName());
            specificContentContainer.removeAll();
            addOptionOrPairButton.setVisible(false);
        }


        buttonLayout.removeAll();
        Button saveButton = new Button("Speichern", VaadinIcon.CHECK.create());
        saveButton.addThemeName("primary");
        saveButton.addClickListener(e -> saveExercise());
        buttonLayout.add(saveButton);

        Button cancelButton = new Button("Abbrechen", VaadinIcon.CLOSE.create());
        cancelButton.addClickListener(e -> {
            if (hasUnsavedChanges) {
                Dialog confirmDialog = new Dialog();
                confirmDialog.setHeaderTitle("Ungespeicherte Änderungen");
                confirmDialog.add("Sie haben ungespeicherte Änderungen. Möchten Sie wirklich abbrechen und diese verwerfen?");

                Button discardButton = new Button("Änderungen verwerfen");
                discardButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
                discardButton.addClickListener(ev -> {
                    confirmDialog.close();
                    exerciseService.getById(currentExercise.getId()).ifPresent(this::displayExerciseDetails);
                    setUnsavedChanges(false);
                });
                Button keepEditingButton = new Button("Weiter bearbeiten");
                keepEditingButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
                keepEditingButton.addClickListener(ev -> confirmDialog.close());

                confirmDialog.getFooter().add(discardButton, keepEditingButton);
                confirmDialog.open();
            } else {
                exerciseService.getById(currentExercise.getId()).ifPresent(this::displayExerciseDetails);
            }
        });
        buttonLayout.add(cancelButton);

        add(buttonLayout);
    }


    private void addChoiceOptionEditor(ChoiceOption option) {
        ChoiceOptionEditor editor = new ChoiceOptionEditor();
        editor.setChoiceOption(option);
        editor.setOnDelete(() -> {
            if (choiceOptionEditors.size() > 1) {
                specificContentContainer.remove(editor);
                choiceOptionEditors.remove(editor);
                setUnsavedChanges(true);
            } else {
                Notification.show("Es muss mindestens eine Antwortmöglichkeit vorhanden sein.", 3000, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        choiceOptionEditors.add(editor);
        specificContentContainer.add(editor);
    }

    private void addAssignmentPairEditor(AssignmentPair pair) {
        AssignmentPairEditor editor = new AssignmentPairEditor();
        editor.setAssignmentPair(pair);
        editor.setOnDelete(() -> {
            if (assignmentPairEditors.size() > 1) {
                specificContentContainer.remove(editor);
                assignmentPairEditors.remove(editor);
                setUnsavedChanges(true);
            } else {
                Notification.show("Es muss mindestens ein Zuordnungspaar vorhanden sein.", 3000, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        assignmentPairEditors.add(editor);
        specificContentContainer.add(editor);
    }

    private void saveExercise() {
        if (!mainBinder.writeBeanIfValid(currentExercise)) {
            Notification.show("Bitte füllen Sie alle Felder korrekt aus.", 3000, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        List<ChoiceOption> collectedChoiceOptions = new ArrayList<>();
        List<AssignmentPair> collectedAssignmentPairs = new ArrayList<>();
        boolean specificOptionsValid = true;

        if (currentExercise instanceof SingleChoice || currentExercise instanceof MultipleChoice) {
            if (choiceOptionEditors.isEmpty()) {
                Notification.show("Es muss mindestens eine Antwortmöglichkeit vorhanden sein.", 3000, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_ERROR);
                specificOptionsValid = false;
            } else {
                for (ChoiceOptionEditor editor : choiceOptionEditors) {
                    if (!editor.isValid()) {
                        Notification.show("Bitte füllen Sie alle Antwortmöglichkeiten korrekt aus.", 3000, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_ERROR);
                        specificOptionsValid = false;
                        break;
                    }
                    collectedChoiceOptions.add(editor.getChoiceOption());
                }
            }
        } else if (currentExercise instanceof AssignmentExercise) {
            if (assignmentPairEditors.isEmpty()) {
                Notification.show("Es muss mindestens ein Zuordnungspaar vorhanden sein.", 3000, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_ERROR);
                specificOptionsValid = false;
            } else {
                for (AssignmentPairEditor editor : assignmentPairEditors) {
                    if (!editor.isValid()) {
                        Notification.show("Bitte füllen Sie alle Zuordnungspaare korrekt aus.", 3000, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_ERROR);
                        specificOptionsValid = false;
                        break;
                    }
                    collectedAssignmentPairs.add(editor.getAssignmentPair());
                }
            }
        }

        if (!specificOptionsValid) {
            return;
        }

        if (currentExercise instanceof SingleChoice) {
            long correctCount = collectedChoiceOptions.stream().filter(ChoiceOption::isCorrect).count();
            if (correctCount != 1) {
                Notification.show("Für Single Choice muss genau eine Antwort richtig markiert sein.", 3000, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }
        } else if (currentExercise instanceof MultipleChoice) {
            long correctCount = collectedChoiceOptions.stream().filter(ChoiceOption::isCorrect).count();
            if (correctCount < 1) {
                Notification.show("Für Multiple Choice muss mindestens eine Antwort richtig markiert sein.", 3000, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }
        }

        currentExercise.setTags(selectedTags);

        if (currentExercise instanceof SingleChoice singleChoice) {
            singleChoice.getChoiceOptions().clear();
            for (ChoiceOption option : collectedChoiceOptions) {
                singleChoice.addChoiceOption(option);
            }
        } else if (currentExercise instanceof MultipleChoice multipleChoice) {
            multipleChoice.getChoiceOptions().clear();
            for (ChoiceOption option : collectedChoiceOptions) {
                multipleChoice.addChoiceOption(option);
            }
        } else if (currentExercise instanceof AssignmentExercise assignment) {
            assignment.getAssignmentPairs().clear();
            for (AssignmentPair pair : collectedAssignmentPairs) {
                assignment.addAssignmentPair(pair);
            }
        }

        try {
            if (currentExercise instanceof SingleChoice singleChoice) {
                singleChoiceService.update(singleChoice);
            } else if (currentExercise instanceof MultipleChoice multipleChoice) {
                multipleChoiceService.update(multipleChoice);
            } else if (currentExercise instanceof AssignmentExercise assignment) {
                assignmentExerciseService.update(assignment);
            } else if (currentExercise instanceof FreetextExercise freetext) {
                freeTextExerciseService.update(freetext);
            }

            exerciseService.getById(currentExercise.getId()).ifPresent(updatedExercise -> {
                this.currentExercise = updatedExercise;
                Notification.show("Aufgabe erfolgreich gespeichert!", 3000, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                displayExerciseDetails(this.currentExercise);
                setUnsavedChanges(false);
            });

        } catch (Exception e) {
            Notification.show("Fehler beim Speichern der Aufgabe: " + e.getMessage(), 5000, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_ERROR);
            e.printStackTrace();
        }
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
                Notification.show("Fehler: Aktueller Benutzer konnte nicht in der Datenbank gefunden werden.", 3000, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            boolean isInstructor = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_INSTRUCTOR"));
            if (!isInstructor) {
                Notification.show("Fehler: Sie haben nicht die erforderliche Berechtigung (Instructor).", 3000, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

        } else {
            Notification.show("Fehler: Aktueller Benutzer konnte nicht ermittelt werden.", 3000, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        final Long finalCurrentUserId = currentUserId;

        List<Exam> exams = examService.getAllExams().stream()
                .filter(exam -> exam.getCreator() != null && exam.getCreator().getId().equals(finalCurrentUserId))
                .filter(exam -> !submissionService.existsByExam(exam))
                .collect(Collectors.toList());

        if (exams.isEmpty()) {
            Notification.show("Es gibt keine verfügbaren Prüfungen, denen diese Aufgabe hinzugefügt werden kann (entweder keine eigenen Prüfungen oder alle bereits geschrieben).", 3000, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_ERROR);
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
                    Notification.show("Bitte wählen Sie mindestens eine Prüfung aus.", 3000, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_ERROR);

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
            if (exam != null && exerciseToAdd != null) {
                Long exerciseId = exerciseToAdd.getId();
                boolean alreadyExists = exerciseId != null &&
                        exam.getExercises().stream()
                                .map(Exercise::getId)
                                .filter(Objects::nonNull)
                                .anyMatch(id -> id.equals(exerciseId));

                if (!alreadyExists) {
                    examService.addExerciseToExam(exam.getId(), exerciseToAdd);
                    Notification.show("Aufgabe " + exerciseToAdd.getExerciseText() + " zu Prüfung " + exam.getTitle() + " hinzugefügt.", 3000, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                } else {
                    Notification.show("Aufgabe " + exerciseToAdd.getExerciseText() + " ist bereits in Prüfung " + exam.getTitle() + ".", 3000, Notification.Position.MIDDLE);
                }
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