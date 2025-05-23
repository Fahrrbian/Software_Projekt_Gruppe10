
package com.gruppe10.examManagement.examAppointment.ui;

import com.gruppe10.base.ui.component.ViewToolbar;
import com.gruppe10.examManagement.exam.domain.Exam;
import com.gruppe10.examManagement.exam.service.ExamService;
import com.gruppe10.examManagement.examAppointment.domain.ExamAppointment;
import com.gruppe10.examManagement.examAppointment.service.ExamAppointmentService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;
import org.springframework.data.domain.Pageable;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

/**
 *Diese Klasse ist die List-View um sich mehrere Prüfungstermine anzeigen zu lassen
 *
 * ToDo: Es muss gefiltert werden, dass es sich nur um dem aktuellen Benutzer zugeordnete Exams in der Auswahl handelt
 * **/

@Route("exam-appointments")
@RouteAlias("exam-appointments/:examId?")
@PageTitle("Prüfungstermine")
@Menu(order = 1, icon = "vaadin:calendar", title = "Prüfungstermine")
@PermitAll
public class ExamAppointmentView extends Main implements HasUrlParameter<Long> {

    private final ExamAppointmentService appointmentService;
    private final ExamService examService;
    private Long currentExamId;

    private final ComboBox<Exam> examSelect;
    private final TextField title;
    private final DateTimePicker appointmentDate;
    private final Button createBtn;
    private final Grid<ExamAppointment> appointmentGrid;

    public ExamAppointmentView(ExamAppointmentService appointmentService,
                               ExamService examService,
                               Clock clock) {
        this.appointmentService = appointmentService;
        this.examService = examService;

        // Exam-Auswahl
        examSelect = new ComboBox<>("Prüfung");
        examSelect.setItems(examService.list(Pageable.unpaged()));
        examSelect.setItemLabelGenerator(Exam::getTitle);
        examSelect.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                currentExamId = e.getValue().getId();
                updateUrl(currentExamId);
                System.out.println("Prüfung ausgewählt mit ID: " + currentExamId);
                refreshGrid();
            }
        });


        // Titel-Eingabe
        title = new TextField();
        title.setPlaceholder("Bezeichnung des Termins");
        title.setAriaLabel("Terminbezeichnung");
        title.setMaxLength(ExamAppointment.DESCRIPTION_MAX_LENGTH);
        title.setMinWidth("20em");

        // Datum und Uhrzeit
        appointmentDate = new DateTimePicker("Termin");
        appointmentDate.setMin(LocalDateTime.now());
        appointmentDate.setStep(Duration.ofMinutes(15));

        // Erstellen-Button
        createBtn = new Button("Termin erstellen", event -> createAppointment());
        createBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        // Grid für die Termine mit Columns Title, Termin und dem Löschen-Button in der Zeile
        var dateTimeFormatter = DateTimeFormatter
                .ofLocalizedDateTime(FormatStyle.MEDIUM)
                .withZone(clock.getZone())
                .withLocale(getLocale());

        appointmentGrid = new Grid<>();
        appointmentGrid.addColumn(ExamAppointment::getTitle)
                .setHeader("Bezeichnung")
                .setSortable(true);
        appointmentGrid.addColumn(appointment ->
                        dateTimeFormatter.format(appointment.getAppointmentDate()))
                .setHeader("Termin")
                .setSortable(true);
        appointmentGrid.addComponentColumn(appointment -> {
            Button deleteButton = new Button("Löschen", e -> {
                appointmentService.deleteAppointment(appointment.getId());
                refreshGrid();
                showNotification("Termin gelöscht", NotificationVariant.LUMO_ERROR);
            });
            deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
            return deleteButton;
        });
        appointmentGrid.setSizeFull();

        //Doppelklick-Listener um Eintrag aufzurufen
        appointmentGrid.addItemDoubleClickListener(event -> {
            if (event.getItem() != null) {
                try {
                    UI.getCurrent().navigate("exam-appointment-form/" + event.getItem().getId()
                    );
                } catch (Exception e) {
                    Notification.show("Fehler beim Öffnen der Prüfung: " + e.getMessage(),
                            3000, Notification.Position.MIDDLE);
                }
            }
        });


        // Layout
        setSizeFull();
        addClassNames(
                LumoUtility.BoxSizing.BORDER,
                LumoUtility.Display.FLEX,
                LumoUtility.FlexDirection.COLUMN,
                LumoUtility.Padding.MEDIUM,
                LumoUtility.Gap.SMALL
        );

        HorizontalLayout inputLayout = new HorizontalLayout(
                examSelect, title, appointmentDate, createBtn
        );
        inputLayout.setAlignItems(FlexComponent.Alignment.BASELINE);

        add(new ViewToolbar("Prüfungstermine", inputLayout));
        add(appointmentGrid);
    }

    //Parameter beim Seitenaufruf verarbeiten
    @Override
    public void setParameter(BeforeEvent event,
                             @OptionalParameter Long examId) {
        if (examId != null) {
            currentExamId = examId;
            Exam exam = examService.getById(examId);
            if (exam != null) {
                examSelect.setValue(exam);
                refreshGrid();
            }
        }
    }

    //Neuen Termin mit ausgewählter Prüfung erstellen
    private void createAppointment() {
        if (examSelect.getValue() == null) {
            showNotification("Bitte wählen Sie eine Prüfung aus",
                    NotificationVariant.LUMO_ERROR);
            return;
        }

        ExamAppointment appointment = new ExamAppointment();
        appointment.setTitle(title.getValue());
        appointment.setAppointmentDate(appointmentDate.getValue()
                .atZone(ZoneId.systemDefault()).toInstant());

        appointmentService.createAppointment(appointment, examSelect.getValue().getId());
        refreshGrid();
        clearInputs();
        showNotification("Termin erstellt", NotificationVariant.LUMO_SUCCESS);
    }

    private void refreshGrid() {
        if (currentExamId != null) {
            var appointments = appointmentService.getAppointmentsByExam(currentExamId);
            appointmentGrid.setItems(appointments);

            if (appointments.isEmpty()) {
                showNotification("Keine Termine für diese Prüfung gefunden",
                        NotificationVariant.LUMO_CONTRAST);
            }
        }
    }

    //Inputfelder leeren
    private void clearInputs() {
        title.clear();
        appointmentDate.clear();
    }

    //URL wird aktualisiert wenn ComboBox ausgewählt wird
    private void updateUrl(Long examId) {
        getUI().ifPresent(ui -> ui.navigate(
                ExamAppointmentView.class, examId
        ));
    }

    private void showNotification(String message, NotificationVariant variant) {
        Notification notification = Notification.show(message, 3000,
                Notification.Position.BOTTOM_END);
        notification.addThemeVariants(variant);
    }
}