package com.gruppe10.examManagement.examAppointment.ui;

import com.gruppe10.base.ui.component.ViewToolbar;
import com.gruppe10.examManagement.exam.service.ExamService;
import com.gruppe10.examManagement.examAppointment.domain.ExamAppointment;
import com.gruppe10.examManagement.examAppointment.domain.StudentData;
import com.gruppe10.examManagement.examAppointment.service.ExamAppointmentService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.router.BeforeEvent;

import java.util.ArrayList;
import java.util.List;

/**
 *Diese Klasse ist das der View um sich einen Prüfungstermin in einer Form anzeigen zu lassen
 * **/

@Route(value = "exam-appointment-form/:id")
@RouteAlias(value = "exam-appointment-form")
@PageTitle("Prüfungstermin")
@Menu(order = 2, icon = "vaadin:calendar-o", title = "Termineditor")
@PermitAll
public class ExamAppointmentFormView extends Main implements HasUrlParameter<Long> {
    private final ExamAppointmentService appointmentService;
    private final ExamService examService;
    private ExamAppointment appointment;
    private TextField title;
    private ExamAppointmentForm form;
    private Button createBtn;
    private Button backBtn;
    private Button saveBtn;
    private Grid<StudentData> studentsGrid;
    private List<StudentData> studentsList = new ArrayList<>();
    private Button importButton;
    private Button saveStudentsBtn; // neues Feld
    private Button resetFormBtn;

    // Konstruktor
    public ExamAppointmentFormView(ExamAppointmentService appointmentService, ExamService examService) {
        this.appointmentService = appointmentService;
        this.examService = examService;
        setSizeFull();
        addClassNames(
                LumoUtility.BoxSizing.BORDER,
                LumoUtility.Display.FLEX,
                LumoUtility.FlexDirection.COLUMN,
                LumoUtility.Padding.MEDIUM,
                LumoUtility.Gap.SMALL
        );
    }

    private void initializeComponents() {
        //Form initialisieren
        form = new ExamAppointmentForm(appointmentService, examService, appointment);

        title = new TextField("Titel");
        String titleValue = (appointment != null && appointment.getTitle() != null)
                ? appointment.getTitle()
                : "";
        title.setValue(titleValue);

        //Buttons für die Toolbar erstellen
        createBtn = new Button("Erstellen", event -> createAppointment());
        createBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        backBtn = new Button("Zurück", event -> navigateBack());
        backBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        backBtn.setIcon(new Icon(VaadinIcon.ARROW_LEFT));

        saveBtn = new Button("Speichern", event -> saveAppointment());
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveBtn.setIcon(new Icon(VaadinIcon.CHECK));


        // Import Button hinzufügen
        importButton = new Button("Prüflinge importieren", event -> openImportDialog());
        importButton.setIcon(new Icon(VaadinIcon.UPLOAD));

        // Grid für Prüflinge erstellen
        studentsGrid = new Grid<>(StudentData.class);
        studentsGrid.addColumn(StudentData::getNachname).setHeader("Name");
        studentsGrid.addColumn(StudentData::getVorname).setHeader("Vorname");
        studentsGrid.addColumn(StudentData::getMatrikelnummer).setHeader("Matrikelnummer");
        studentsGrid.setItems(studentsList);

        //Button zum Speichern der Prüflinge
        saveStudentsBtn = new Button("Prüflinge speichern", event -> saveStudents());
        saveStudentsBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveStudentsBtn.setIcon(new Icon(VaadinIcon.USERS));

        //Button zum Clearen des Formulars
        resetFormBtn = new Button("Zurücksetzen", event -> {
            form.resetForm();
            appointment = new ExamAppointment();
        });
        resetFormBtn.addThemeVariants(ButtonVariant.LUMO_ERROR);

        //View zusammenbauen
        removeAll();
        add(new ViewToolbar("Termin bearbeiten",
                ViewToolbar.group(backBtn, saveBtn, importButton, saveStudentsBtn, resetFormBtn)));
        add(form);
        add(new H3("Zugeordnete Prüflinge"));
        add(studentsGrid);


    }

    @Override
    public void setParameter(BeforeEvent event, Long parameter) {
        System.out.println("Parameter erhalten: " + parameter); //Zum testen

        try {
            this.appointment = parameter != null ?
                    appointmentService.getAppointmentById(parameter).orElse(new ExamAppointment()) :
                    new ExamAppointment();
        } catch (Exception e) {
            this.appointment = new ExamAppointment();
        }
        initializeComponents();
        loadStudentData();
    }


    private void createAppointment() {
        appointment = new ExamAppointment();
        form = new ExamAppointmentForm(appointmentService, examService, appointment);
        refreshForm();
    }

    private void navigateBack() {
        UI.getCurrent().navigate("exam-appointments/" + appointment.getExam().getId());
    }

    //Änderungen der Daten in Datenbank schreiben
    private void saveAppointment() {
        try {
            if (appointment != null && form.getSelectedExamId() != null) {
                appointment.setTitle(form.getTitle());
                appointment.setAppointmentDate(form.getAppointmentDate());

                if (appointment.getId() == null) {
                    appointmentService.createAppointment(appointment, form.getSelectedExamId());
                } else {
                    appointment = appointmentService.updateAppointment(appointment.getId(), appointment);
                }

                Notification.show("Termin erfolgreich gespeichert",
                                3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

                navigateBack();
            } else {
                Notification.show("Bitte wählen Sie eine Prüfung aus",
                                3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
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
            if (appointment != null && appointment.getId() != null) {
                appointmentService.saveStudentDataForAppointment(appointment.getId(), studentsList);
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
        if (appointment != null && appointment.getId() != null) {
            try {
                List<StudentData> loadedStudents = appointmentService.getStudentDataForAppointment(appointment.getId());
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


    private void refreshForm() {
        removeAll();
        initializeComponents();
    }

}