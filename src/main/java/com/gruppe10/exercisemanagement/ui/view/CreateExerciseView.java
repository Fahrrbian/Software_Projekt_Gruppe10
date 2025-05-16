package com.gruppe10.exercisemanagement.ui.view;

import com.gruppe10.base.ui.view.MainLayout;
import com.gruppe10.exercisemanagement.domain.*;
import com.gruppe10.exercisemanagement.service.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
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
public class CreateExerciseView extends Div {

    private final FreetextExerciseService freetextExerciseService;
    private final SingleChoiceService singlechoiceService;
    private final MultipleChoiceService multipleChoiceService;
    private final TagService tagService;
    private final AssignmentExerciseService assignmentExerciseService;

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

        typDropdown.setItems("Freitextaufgabe", "Single Choice", "Multiple Choice", "Zuordnungsaufgabe");
        typDropdown.addValueChangeListener(event -> updateSpecificContent(event.getValue()));
        typDropdown.setValue("Freitextaufgabe");

        exerciseTextField.setWidthFull();
        exerciseTextField.setPlaceholder("Aufgabenstellung eingeben");

        scoreField.setPlaceholder("z.B. 10");
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
                List<Tag> currentItems = new ArrayList<>(tagSelector.getSelectedItems());
                currentItems.add(newTag);
                tagSelector.setItems(currentItems);
                selectedTags.add(newTag);
                tagSelector.select(selectedTags);
            }
        });

        tagSelector.addValueChangeListener(event -> {
            selectedTags.addAll(event.getValue());
        });

        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(event -> save());

        add(typDropdown, exerciseTextField, scoreField, tagSelector, specificContent, examButton, saveButton);

        updateSpecificContent(typDropdown.getValue());
    }

    private void updateSpecificContent(String exerciseType) {
        specificContent.removeAll();
        choiceOptionsLayout.removeAll();
        choiceOptionEditors.clear();

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
        choiceOptionEditors.add(editor);
        choiceOptionsLayout.add(editor);
    }

    private void addAssignmentPairEditor() {
        AssignmentPairEditor editor = new AssignmentPairEditor();
        assignmentPairEditors.add(editor);
        assignmentPairsLayout.add(editor);
    }

    private void save() {
        String exerciseType = typDropdown.getValue();
        String exerciseText = exerciseTextField.getValue();
        String scoreStr = scoreField.getValue();

        if (exerciseText == null || exerciseText.isEmpty()) {
            Notification.show("Bitte eine Aufgabenstellung eingeben.");
            return;
        }

        try {
            int score = Integer.parseInt(scoreStr);

            switch (exerciseType) {
                case "Freitextaufgabe":
                    FreetextExercise freetextExercise = new FreetextExercise();
                    freetextExercise.setExerciseText(exerciseText);
                    freetextExercise.setScore(score);
                    freetextExercise.setTags(selectedTags);
                    freetextExerciseService.createFreetextExercise(freetextExercise);
                    Notification.show("Freitextaufgabe erfolgreich gespeichert!");
                    break;
                case "Single Choice":
                    if (choiceOptionEditors.stream().filter(ChoiceOptionEditor::isCorrect).count() != 1) {
                        Notification.show("Bitte genau eine richtige Antwortmöglichkeit für Single Choice auswählen.");
                        return;
                    }
                    SingleChoice singlechoice = new SingleChoice();
                    singlechoice.setExerciseText(exerciseText);
                    singlechoice.setScore(score);
                    choiceOptionEditors.forEach(editor -> {
                        ChoiceOption backendOption = new ChoiceOption(editor.getAnswerText(), editor.isCorrect(), singlechoice);
                        singlechoice.getChoiceOptions().add(backendOption);
                    });
                    singlechoice.setTags(selectedTags);
                    singlechoiceService.create(singlechoice);
                    Notification.show("Single Choice Aufgabe erfolgreich gespeichert!");
                    break;
                case "Multiple Choice":
                    if (choiceOptionEditors.stream().noneMatch(ChoiceOptionEditor::isCorrect)) {
                        Notification.show("Bitte mindestens eine richtige Antwortmöglichkeit für Multiple Choice auswählen.");
                        return;
                    }
                    MultipleChoice multipleChoice = new MultipleChoice();
                    multipleChoice.setExerciseText(exerciseText);
                    multipleChoice.setScore(score);
                    choiceOptionEditors.forEach(editor -> {
                        ChoiceOption backendOption = new ChoiceOption(editor.getAnswerText(), editor.isCorrect(), multipleChoice);
                        multipleChoice.getChoiceOptions().add(backendOption);
                    });
                    multipleChoice.setTags(selectedTags);
                    multipleChoiceService.create(multipleChoice);
                    Notification.show("Multiple Choice Aufgabe erfolgreich gespeichert!");
                    break;
                case "Zuordnungsaufgabe":
                    AssignmentExercise assignmentExercise = new AssignmentExercise();
                    assignmentExercise.setExerciseText(exerciseText);
                    assignmentExercise.setScore(score);
                    assignmentExercise.setTags(selectedTags);
                    for (AssignmentPairEditor editor : assignmentPairEditors) {
                        String partOne = editor.getPartOne().trim();
                        String partTwo = editor.getPartTwo().trim();
                        if (!partOne.isEmpty() && !partTwo.isEmpty()) {
                            AssignmentPair pair = new AssignmentPair();
                            pair.setPartOne(partOne);
                            pair.setPartTwo(partTwo);
                            assignmentExercise.addAssignmentPair(pair);
                        }
                    }
                    assignmentExerciseService.create(assignmentExercise);
                    Notification.show("Zuordnungsaufgabe erfolgreich gespeichert!");
                    break;
            }
            clearInputFields();
        } catch (NumberFormatException e) {
            Notification.show("Bitte gültige Punktzahl eingeben.");
        }
    }

    private void clearInputFields() {
        exerciseTextField.clear();
        scoreField.clear();
        updateSpecificContent(typDropdown.getValue());
        tagSelector.clear();
        selectedTags.clear();
        typDropdown.setValue("Freitextaufgabe");
    }
}