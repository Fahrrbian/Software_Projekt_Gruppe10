package com.gruppe10.exercisemanagement.ui.view;

import com.gruppe10.base.ui.Layout.MainLayout;
import com.gruppe10.base.ui.Layout.TimedMainLayout;
import com.gruppe10.exercisemanagement.domain.*;
import com.gruppe10.exercisemanagement.service.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Route(value="create-exercise", layout = MainLayout.class)
@RolesAllowed("INSTRUCTOR")
public class CreateExerciseView extends VerticalLayout {

    private final FreetextExerciseService freetextExerciseService;
    private final SingleChoiceService singlechoiceService;
    private final MultipleChoiceService multipleChoiceService;
    private final TagService tagService;
    private final AssignmentExerciseService assignmentExerciseService;

    private final Binder<Exercise> binder = new Binder<>();

    private final TextArea exerciseTextField = new TextArea("Aufgabenstellung eingeben");
    private final TextField scoreField = new TextField("Mögliche Punkte");
    private final ComboBox<String> typDropdown = new ComboBox<>("Aufgabentyp");
    private final Div specificContent = new Div();
    private final Button examButton = new Button("Zu einer Prüfung hinzufügen...");
    private final Button saveButton = new Button("Speichern");
    private final MultiSelectComboBox<Tag> tagSelector = new MultiSelectComboBox<>("Tags");
    private final Set<Tag> selectedTags = new HashSet<>();

    private final VerticalLayout choiceOptionsLayout = new VerticalLayout();
    private final List<ChoiceOptionEditor> choiceOptionEditors = new ArrayList<>();

    private final VerticalLayout assignmentPairsLayout = new VerticalLayout();
    private final List<AssignmentPairEditor> assignmentPairEditors = new ArrayList<>();

    @Autowired
    public CreateExerciseView(FreetextExerciseService freetextExerciseService, SingleChoiceService singlechoiceService, MultipleChoiceService multipleChoiceService, TagService tagService, AssignmentExerciseService assignmentExerciseService) {
        this.freetextExerciseService = freetextExerciseService;
        this.singlechoiceService = singlechoiceService;
        this.multipleChoiceService = multipleChoiceService;
        this.tagService = tagService;
        this.assignmentExerciseService = assignmentExerciseService;

        setSizeFull();
        addClassNames(
                LumoUtility.Padding.MEDIUM,
                LumoUtility.Display.FLEX,
                LumoUtility.FlexDirection.COLUMN,
                LumoUtility.Gap.LARGE
        );

        add(new H2("Aufgabe erstellen"));

        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("600px", 2)
        );

        typDropdown.setItems("Freitextaufgabe", "Single Choice", "Multiple Choice", "Zuordnungsaufgabe");
        typDropdown.addValueChangeListener(event -> updateSpecificContent(event.getValue()));
        typDropdown.setValue("Freitextaufgabe");

        exerciseTextField.setWidthFull();
        exerciseTextField.setPlaceholder("Aufgabenstellung eingeben");

        scoreField.setPlaceholder("Punkte");
        scoreField.setWidth("200px");

        tagSelector.setItems(tagService.getAll());
        tagSelector.setItemLabelGenerator(Tag::getName);
        tagSelector.setClearButtonVisible(true);
        tagSelector.setAllowCustomValue(true);
        tagSelector.setPlaceholder("Tags auswählen oder neu eingeben...");
        tagSelector.setWidthFull();

        tagSelector.addCustomValueSetListener(event -> {
            String newTagName = event.getDetail().trim();
            if (!newTagName.isEmpty()) {
                Tag newTag = tagService.findOrCreateByName(newTagName);
                tagSelector.setItems(tagService.getAll());
                selectedTags.add(newTag);
                tagSelector.select(selectedTags);
            }
        });

        tagSelector.addValueChangeListener(event -> {
            selectedTags.addAll(event.getValue());
        });

        specificContent.setWidthFull();
        specificContent.getStyle().set("padding", "0");
        specificContent.getStyle().set("margin", "0");

        choiceOptionsLayout.setPadding(false);
        choiceOptionsLayout.setMargin(false);
        choiceOptionsLayout.setSpacing(false);

        assignmentPairsLayout.setPadding(false);
        assignmentPairsLayout.setMargin(false);
        assignmentPairsLayout.setSpacing(false);

        formLayout.add(typDropdown, scoreField, exerciseTextField, tagSelector);
        add(formLayout, specificContent, saveButton);

        binder.forField(exerciseTextField)
                .asRequired("Aufgabentext darf nicht leer sein")
                .bind(Exercise::getExerciseText, Exercise::setExerciseText);

        binder.forField(scoreField)
                .asRequired("Punkte eingeben")
                .withConverter(new StringToIntegerConverter("Bitte eine gültige Zahl eingeben"))
                .withValidator(score -> score != null && score > 0, "Punktzahl muss größer 0 sein")
                .bind(Exercise::getScore, Exercise::setScore);

        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(event -> save());

        updateSpecificContent(typDropdown.getValue());
    }

    private void updateSpecificContent(String exerciseType) {
        exerciseTextField.setInvalid(false);
        scoreField.setInvalid(false);
        specificContent.removeAll();
        choiceOptionsLayout.removeAll();
        choiceOptionEditors.clear();
        assignmentPairsLayout.removeAll();
        assignmentPairEditors.clear();

        if ("Single Choice".equals(exerciseType) || "Multiple Choice".equals(exerciseType)) {
            specificContent.add(choiceOptionsLayout);
            addChoiceOptionEditor();
            Button addOptionButton = new Button("+ Antwortmöglichkeit hinzufügen", event -> addChoiceOptionEditor());
            specificContent.add(addOptionButton);
        }
        else if ("Zuordnungsaufgabe".equals(exerciseType)) {
            specificContent.add(assignmentPairsLayout);
            addAssignmentPairEditor();
            Button addPairButton = new Button("+ Zuordnungspaar hinzufügen", event -> addAssignmentPairEditor());
            specificContent.add(addPairButton);
        }
    }

    private void addChoiceOptionEditor() {
        ChoiceOptionEditor editor = new ChoiceOptionEditor();
        editor.setOnDelete(() -> {
            choiceOptionsLayout.remove(editor);
            choiceOptionEditors.remove(editor);
        });
        choiceOptionEditors.add(editor);
        choiceOptionsLayout.add(editor);
    }

    private void addAssignmentPairEditor() {
        AssignmentPairEditor editor = new AssignmentPairEditor();
        editor.setOnDelete(() -> {
            assignmentPairsLayout.remove(editor);
            assignmentPairEditors.remove(editor);
        });
        assignmentPairEditors.add(editor);
        assignmentPairsLayout.add(editor);
    }

    private void save() {
        String selectedType = typDropdown.getValue();
        Exercise baseExercise;

        switch (selectedType) {
            case "Freitextaufgabe" -> baseExercise = new FreetextExercise();
            case "Single Choice" -> baseExercise = new SingleChoice();
            case "Multiple Choice" -> baseExercise = new MultipleChoice();
            case "Zuordnungsaufgabe" -> baseExercise = new AssignmentExercise();
            default -> {
                Notification.show("Ungültiger Aufgabentyp.");
                return;
            }
        }

        boolean mainExerciseValid = binder.writeBeanIfValid(baseExercise);

        boolean specificOptionsValid = true;

        if (baseExercise instanceof SingleChoice single) {
            for (ChoiceOptionEditor editor : choiceOptionEditors) {
                if (!editor.isValid()) {
                    specificOptionsValid = false;
                }
            }
            if (specificOptionsValid) {
                long correctCount = choiceOptionEditors.stream().filter(ChoiceOptionEditor::isCorrect).count();
                if (correctCount != 1) {
                    Notification.show("Für Single Choice muss genau eine Antwort richtig sein.");
                    specificOptionsValid = false;
                }
            }
        }
        else if (baseExercise instanceof MultipleChoice multiple) {
            for (ChoiceOptionEditor editor : choiceOptionEditors) {
                if (!editor.isValid()) {
                    specificOptionsValid = false;
                }
            }
            if (specificOptionsValid) {
                if (choiceOptionEditors.stream().noneMatch(ChoiceOptionEditor::isCorrect)) {
                    Notification.show("Für Multiple Choice muss mindestens eine Antwort richtig sein.");
                    specificOptionsValid = false;
                }
            }
        }
        else if (baseExercise instanceof AssignmentExercise assign) {
            for (AssignmentPairEditor editor : assignmentPairEditors) {
                if (!editor.isValid()) {
                    specificOptionsValid = false;
                }
            }
        }

        if (!mainExerciseValid || !specificOptionsValid) {
            return;
        }

        baseExercise.setTags(selectedTags);

        if (baseExercise instanceof SingleChoice single) {
            choiceOptionEditors.stream()
                    .map(ChoiceOptionEditor::getChoiceOption)
                    .forEach(single::addChoiceOption);
            singlechoiceService.create(single);
        }
        else if (baseExercise instanceof MultipleChoice multiple) {
            choiceOptionEditors.stream()
                    .map(ChoiceOptionEditor::getChoiceOption)
                    .forEach(multiple::addChoiceOption);
            multipleChoiceService.create(multiple);
        }
        else if (baseExercise instanceof AssignmentExercise assign) {
            assignmentPairEditors.stream()
                    .map(AssignmentPairEditor::getAssignmentPair)
                    .forEach(assign::addAssignmentPair);
            assignmentExerciseService.create(assign);
        } else if (baseExercise instanceof FreetextExercise freetext) {
            freetextExerciseService.createFreetextExercise(freetext);
        }

        Notification.show("Aufgabe gespeichert!");
        clearInputFields();
    }

    private void clearInputFields() {
        exerciseTextField.clear();
        exerciseTextField.setInvalid(false);
        scoreField.setInvalid(false);
        scoreField.clear();
        updateSpecificContent(typDropdown.getValue());
        tagSelector.clear();
        selectedTags.clear();
        typDropdown.setValue("Freitextaufgabe");
    }
}