package com.gruppe10.examManagement.exam.ui.FormView;

/**
 * Author: Henrik Struckmeier
 * Date: 02/05/2025
 **/


import com.gruppe10.base.ui.Layout.MainLayout;
import com.gruppe10.examManagement.exam.domain.Exam;
import com.gruppe10.examManagement.exam.service.ExamService;
import com.gruppe10.examManagement.examAppointment.domain.StudentData;
import com.gruppe10.examManagement.examAppointment.ui.ExcelImportDialog;
import com.gruppe10.exercisemanagement.service.ExerciseService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.ServletConfig;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
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
    private Exam exam;
    private TextField title;
    private PruefungForm form;
    private ExerciseGrid exerciseGrid;
    private Button createBtn;
    private Button backBtn;
    private Button saveBtn;
    private Button importButton;
    private Grid<StudentData> studentsGrid;
    private List<StudentData> studentsList = new ArrayList<>();


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
        title.setValue(exam != null ? exam.getTitle() : "");

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

        // Import Button hinzufügen
        importButton = new Button("Prüflinge importieren", event -> openImportDialog());
        importButton.setIcon(new Icon(VaadinIcon.UPLOAD));

        Button terminButton = new Button("Zu den Prüfungsterminen");
        terminButton.addClickListener(e -> {
            terminButton.getUI().ifPresent(ui ->
                    ui.navigate("exam-appointments/" + exam.getId())
            );
        });

        form = new PruefungForm(examService, exam);

        // Grid für Prüflinge erstellen
        studentsGrid = new Grid<>();
        studentsGrid.addColumn(StudentData::getNachname).setHeader("Name");
        studentsGrid.addColumn(StudentData::getVorname).setHeader("Vorname");
        studentsGrid.addColumn(StudentData::getMatrikelnummer).setHeader("Matrikelnummer");
        studentsGrid.setHeight("200px"); // Gleiche Höhe wie ExerciseGrid


        removeAll(); // Entfernt alle vorherigen Komponenten
        HorizontalLayout buttonLayout = new HorizontalLayout(backBtn, saveBtn);
        buttonLayout.setAlignItems(Alignment.BASELINE);
        buttonLayout.setPadding(true);

        add(buttonLayout);
        add(form);
        // Horizontales Layout für die beiden Grids
        HorizontalLayout gridsLayout = new HorizontalLayout();
        gridsLayout.setSizeFull();

        // Vertikales Layout für Exercise-Teil
        VerticalLayout exerciseLayout = new VerticalLayout();
        exerciseLayout.add(new H3("Aufgaben"));
        exerciseLayout.add(exerciseGrid);
        exerciseLayout.setWidth("50%");

        // Vertikales Layout für Prüflinge-Teil
        VerticalLayout studentsLayout = new VerticalLayout();
        studentsLayout.add(new H3("Prüflinge"));
        studentsLayout.add(importButton);
        studentsLayout.add(studentsGrid);
        studentsLayout.setWidth("50%");


        gridsLayout.add(exerciseLayout, studentsLayout);
        add(gridsLayout);

        if (exam != null && exam.getId() != null) {
            exerciseGrid.setPruefungId(exam.getId());
        }

    }

    @Override
    public void setParameter(BeforeEvent event, Long parameter) {
        if (parameter == null) {
            // Neuer Modus: Es wurde keine ID übergeben → neue Exam anlegen
            this.exam = new Exam();
        } else {
            // Bearbeiten-Modus: lade die Exam oder wirf 404
            Optional<Exam> opt = examService.getById(parameter);
            if (opt.isEmpty()) {
                // Wenn die ID nicht existiert, lehne mit 404 ab.
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
            this.exam = opt.get();
        }

        initializeComponents();
        if (exam != null && exam.getId() != null) {
            loadStudentData(); // Lädt die Prüflingsdaten
        }

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
            if (exam != null) {
                exam.setTitle(form.getTitle());
                exam.setBestehensgrenze(form.getBestehensgrenze());
                exam.setGesamtpunkte(form.getGesamtpunkte());
                saveStudents();

                examService.updatePruefung(exam, exam.getId());

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

    //Änderungen an der Prüflingsliste in Datenbank schreiben
    //Beim erstellen eines neuen Termins wird geprüft ob der Termin zuerst gespeichert wurde
    private void saveStudents() {
        try {
            if (exam != null && exam.getId() != null) {
                examService.saveStudentDataForExam(exam.getId(), studentsList);
                Notification.show("Prüflinge erfolgreich gespeichert",
                                3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } else {
                Notification.show("Bitte speichern Sie zuerst den Termin",
                                3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        } catch (Exception e) {
            Notification.show("Fehler beim Speichern der Prüflinge: " + e.getMessage(),
                            3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    //Importdialog öffnen und grid aktualisieren
    //Die Liste mit StudentData-Objekten wird strukturiert in die Grid geschrieben
    private void openImportDialog() {
        ExcelImportDialog dialog = new ExcelImportDialog(studentData -> {
            studentsList.clear();
            studentsList.addAll(studentData);
            studentsGrid.setItems(studentsList);

            // Grid-Spalten aktualisieren
            studentsGrid.removeAllColumns();
            studentsGrid.addColumn(StudentData::getNachname).setHeader("Name");
            studentsGrid.addColumn(StudentData::getVorname).setHeader("Vorname");
            studentsGrid.addColumn(StudentData::getMatrikelnummer).setHeader("Matrikelnummer");

            Notification.show(studentData.size() + " Prüflinge importiert",
                            3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        });
        dialog.open();
    }

    //Daten aus der aus Excel erzeugten Liste laden
    private void loadStudentData() {
        if (exam != null && exam.getId() != null) {
            try {
                List<StudentData> loadedStudents = examService.getStudentDataForExam(exam.getId());
                studentsList.clear();
                studentsList.addAll(loadedStudents);
                studentsGrid.setItems(studentsList);
            } catch (Exception e) {
                Notification.show("Fehler beim Laden der Prüflinge: " + e.getMessage(),
                                3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        }
    }

}