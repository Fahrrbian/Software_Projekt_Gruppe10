package com.gruppe10.examManagement.examsToCorrect.ui;

import com.gruppe10.base.ui.component.ViewToolbar;
import com.gruppe10.examManagement.examAppointment.domain.ExamAppointment;
import com.gruppe10.examManagement.examsToCorrect.service.ExamsToCorrectService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Clock;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

@Route(value = "exams-to-correct")
@PageTitle("Zu korrigierende Prüfungen")
@RolesAllowed("INSTRUCTOR")
public class ExamsToCorrectView extends VerticalLayout {

    private final ExamsToCorrectService examsToCorrectService;
    final Grid<ExamAppointment> examGrid;

    @Autowired
    public ExamsToCorrectView(ExamsToCorrectService examsToCorrectService, Clock clock) {
        this.examsToCorrectService = examsToCorrectService;

        var dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                .withZone(clock.getZone())
                .withLocale(getLocale());

        examGrid = new Grid<>();
        examGrid.setItems(examsToCorrectService.getOpenToCorrectAppointments());
        
        // Spalten für die Grid-Ansicht definieren
        examGrid.addColumn(ExamAppointment::getId).setHeader("ID");
        examGrid.addColumn(ExamAppointment::getTitle).setHeader("Terminbezeichnung");
        examGrid.addColumn(appointment -> appointment.getExam().getTitle())
                .setHeader("Prüfung");
        examGrid.addColumn(appointment -> dateTimeFormatter.format(appointment.getAppointmentDate()))
                .setHeader("Prüfungstermin");
        examGrid.addColumn(appointment -> appointment.getStudentExamAppointments().size())
                .setHeader("Anzahl Teilnehmer");

        // Doppelklick-Listener für Details
        examGrid.addItemDoubleClickListener(event -> {
            if (event.getItem() != null) {
                try {
                    // Hier können Sie zur Detail-Ansicht navigieren
                    UI.getCurrent().navigate("exam-correction/" + event.getItem().getId());
                } catch (Exception e) {
                    Notification.show("Fehler beim Öffnen der Prüfung: " + e.getMessage(),
                            3000, Notification.Position.MIDDLE);
                }
            }
        });

        examGrid.setSizeFull();

        // Layout-Einstellungen
        setSizeFull();
        addClassNames(
                LumoUtility.BoxSizing.BORDER,
                LumoUtility.Display.FLEX,
                LumoUtility.FlexDirection.COLUMN,
                LumoUtility.Padding.MEDIUM,
                LumoUtility.Gap.SMALL
        );

        // Komponenten zum Layout hinzufügen
        add(new ViewToolbar("Zu korrigierende Prüfungen"));
        add(examGrid);
    }

    // Methode zum Aktualisieren der Grid-Daten
    public void refreshGrid() {
        examGrid.setItems(examsToCorrectService.getOpenToCorrectAppointments());
    }
}