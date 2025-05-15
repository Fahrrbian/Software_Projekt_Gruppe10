package com.gruppe10.exercisemanagement.ui.view;

import com.gruppe10.exercisemanagement.domain.FreetextExercise;
import com.gruppe10.exercisemanagement.service.FreetextExerciseService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

@Route("create-exercise")
public class CreateExerciseView extends Div {

    private final FreetextExerciseService freetextExerciseService;

    // UI Komponenten
    private final TextArea aufgabenstellungField = new TextArea("Aufgabenstellung eingeben");
    private final TextField punkteField = new TextField("Mögliche Punkte");
    private final ComboBox<String> typDropdown = new ComboBox<>();
    private final Button pruefungButton = new Button("Zu einer Prüfung hinzufügen...");
    private final Button speichernButton = new Button("Speichern");

    public CreateExerciseView(FreetextExerciseService freetextExerciseService) {
        this.freetextExerciseService = freetextExerciseService;

        setSizeFull();
        addClassNames(
                LumoUtility.Padding.MEDIUM,
                LumoUtility.Display.FLEX,
                LumoUtility.FlexDirection.COLUMN,
                LumoUtility.Gap.LARGE
        );

        add(new H2("Aufgabe erstellen"));

        typDropdown.setLabel("Aufgabentyp");
        typDropdown.setItems("Freitextaufgabe", "Single Choice", "Multiple Choice", "Zuordnung");
        typDropdown.setValue("Freitextaufgabe"); // default

        aufgabenstellungField.setWidthFull();
        aufgabenstellungField.setPlaceholder("Aufgabenstellung eingeben");

        punkteField.setPlaceholder("z.B. 10");
        punkteField.setWidth("200px");

        speichernButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        speichernButton.addClickListener(event -> speichern());

        // Layout hinzufügen
        add(typDropdown, aufgabenstellungField, punkteField, pruefungButton, speichernButton);
    }

    private void speichern() {
        String aufgabe = aufgabenstellungField.getValue();
        String punkteStr = punkteField.getValue();

        if (aufgabe == null || aufgabe.isEmpty()) {
            Notification.show("Bitte eine Aufgabenstellung eingeben.");
            return;
        }

        try {
            int punkte = Integer.parseInt(punkteStr);
            FreetextExercise exercise = new FreetextExercise();
            exercise.setExerciseText(aufgabe);
            exercise.setScore(punkte);

            freetextExerciseService.createFreetextExercise(exercise);

            Notification.show("Aufgabe erfolgreich gespeichert!");
            aufgabenstellungField.clear();
            punkteField.clear();
        } catch (NumberFormatException e) {
            Notification.show("Bitte gültige Punktzahl eingeben.");
        }
    }
}
