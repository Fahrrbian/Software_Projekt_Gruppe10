package com.gruppe10.examManagement.examsToCorrect.ui;

import com.gruppe10.base.ui.Layout.MainLayout;
import com.gruppe10.base.ui.component.ViewToolbar;
import com.gruppe10.examManagement.exam.domain.Exam;
import com.gruppe10.examManagement.examsToCorrect.service.ExamsToCorrectService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Clock;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

@Route(value = "exams-to-correct", layout = MainLayout.class)
@PageTitle("Zu korrigierende Prüfungen")
@Menu(order = 6, icon = "vaadin:clipboard-check", title = "Prüfungskorrektur")
@RolesAllowed("INSTRUCTOR")
public class ExamsToCorrectView extends VerticalLayout {

    private final ExamsToCorrectService examsToCorrectService;
    final Grid<Exam> examGrid;

    @Autowired
    public ExamsToCorrectView(ExamsToCorrectService examsToCorrectService, Clock clock) {
        this.examsToCorrectService = examsToCorrectService;

        var dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                .withZone(clock.getZone())
                .withLocale(getLocale());

        examGrid = new Grid<>();
        examGrid.setItems(examsToCorrectService.getOpenToCorrectExams());
        
        // Spalten für die Grid-Ansicht definieren
        examGrid.addColumn(Exam::getId).setHeader("ID");
        examGrid.addColumn(Exam::getTitle).setHeader("Terminbezeichnung");
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

        // Komponenten zum Layout hinzufügen
        add(new H2("Zu korrigierende Prüfungen"));

        add(examGrid);
    }

    // Methode zum Aktualisieren der Grid-Daten
    public void refreshGrid() {
        examGrid.setItems(examsToCorrectService.getOpenToCorrectExams());
    }
}