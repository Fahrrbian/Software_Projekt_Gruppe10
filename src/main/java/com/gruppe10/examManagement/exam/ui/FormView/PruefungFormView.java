package com.gruppe10.examManagement.exam.ui.FormView;

/**
 * Author: Henrik Struckmeier
 * Date: 02/05/2025
 **/


import com.gruppe10.base.ui.Layout.MainLayout;
import com.gruppe10.base.ui.component.ViewToolbar;
import com.gruppe10.examManagement.exam.domain.Exam;
import com.gruppe10.examManagement.exam.service.ExamService;
import com.gruppe10.exercisemanagement.service.ExerciseService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.ServletConfig;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Route(value = "pruefung-form/:id", layout = MainLayout.class)
@RouteAlias(value = "pruefung-form", layout = MainLayout.class)
@PageTitle("Prüfung")
@Menu(order = 5, icon = "vaadin:form", title = "Prüfungserstellung")
@PermitAll
public class PruefungFormView extends VerticalLayout implements HasUrlParameter<Long> {

    private final ExamService examService;
    private final ServletConfig servletConfig;
    private final ExerciseService exerciseService;
    private Exam IExamInterface;
    private TextField title;
    private PruefungForm form;
    private ExerciseGrid exerciseGrid;
    private Button createBtn;
    private Button backBtn;
    private Button saveBtn;


    public PruefungFormView(ExamService examService, ExerciseService exerciseService, ServletConfig servletConfig) {
        this.examService = examService;
        this.exerciseGrid = new ExerciseGrid(exerciseService);
        this.exerciseService = exerciseService;
        this.servletConfig = servletConfig;

        setSizeFull();
        addClassNames(LumoUtility.BoxSizing.BORDER,
                LumoUtility.Display.FLEX,
                LumoUtility.FlexDirection.COLUMN,
                LumoUtility.Padding.MEDIUM,
                LumoUtility.Gap.SMALL);

    }

    private void initializeComponents() {
        title = new TextField("Titel");
        title.setValue(IExamInterface != null ? IExamInterface.getTitle() : "");

        createBtn = new Button("Create", event -> createPruefung());
        createBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        // Zurück-Button erstellen
        backBtn = new Button("Zurück", event -> navigateBack());
        backBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        backBtn.setIcon(new Icon(VaadinIcon.ARROW_LEFT));

        // Speichern-Button erstellen
        saveBtn = new Button("Speichern", event -> savePruefung());
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveBtn.setIcon(new Icon(VaadinIcon.CHECK));

        Button terminButton = new Button("Zu den Prüfungsterminen");
        terminButton.addClickListener(e -> {
            terminButton.getUI().ifPresent(ui ->
                    ui.navigate("exam-appointments/" + IExamInterface.getId())
            );
        });

        form = new PruefungForm(examService, IExamInterface);

        removeAll(); // Entfernt alle vorherigen Komponenten
        add(new ViewToolbar("Prüfung bearbeiten",
                ViewToolbar.group(backBtn, title, createBtn, saveBtn, terminButton)));
        add(form);
        add(exerciseGrid);

        if (IExamInterface != null && IExamInterface.getId() != null) {
            exerciseGrid.setPruefungId(IExamInterface.getId());
        }

    }

    @Override
    public void setParameter(BeforeEvent event, Long parameter) {
        if (parameter == null) {
            // Neuer Modus: Es wurde keine ID übergeben → neue Exam anlegen
            this.IExamInterface = new Exam();
        } else {
            // Bearbeiten-Modus: lade die Exam oder wirf 404
            Optional<Exam> opt = examService.getById(parameter);
            if (opt.isEmpty()) {
                // Wenn die ID nicht existiert, lehne mit 404 ab.
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
            this.IExamInterface = opt.get();
        }

        initializeComponents();
    }

        /*try {
            this.IExamInterface = parameter != null ?
                    examService.getById(parameter) : getById hatte bei mir zu einem Typ-Exception geführt: java: Inkompatible Typen: Ungültiger Typ in Bedingungsausdruck
                                                         java.util.Optional<com.gruppe10.examManagement.exam.domain.Exam> kann nicht in com.gruppe10.examManagement.exam.domain.Exam konvertiert werden
                    new Exam();
        } catch (Exception e) {
            this.IExamInterface = new Exam();
        }
        initializeComponents();
    }*/

    private void createPruefung() {
        Exam IExamInterface = new Exam();
    }

    private void navigateBack() {
        UI.getCurrent().navigate("pruefung-list");
    }

    private void savePruefung() {
        try {
            if (IExamInterface != null) {
                IExamInterface.setTitle(form.getTitle());
                IExamInterface.setBestehensgrenze(form.getBestehensgrenze());
                IExamInterface.setGesamtpunkte(form.getGesamtpunkte());

                examService.updatePruefung(IExamInterface, IExamInterface.getId());

                Notification.show("Prüfung erfolgreich gespeichert",
                                3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            }
        } catch (Exception e) {
            Notification.show("Fehler beim Speichern: " + e.getMessage(),
                            3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}