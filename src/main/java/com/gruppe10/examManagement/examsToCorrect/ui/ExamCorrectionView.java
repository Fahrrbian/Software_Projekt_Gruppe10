package com.gruppe10.examManagement.examsToCorrect.ui;

import com.gruppe10.base.ui.Layout.MainLayout;
import com.gruppe10.base.ui.component.ViewToolbar;
import com.gruppe10.examManagement.examAppointment.domain.ExamAppointment;
import com.gruppe10.examManagement.examAppointment.domain.StudentExamAppointment;
import com.gruppe10.examManagement.examAppointment.service.ExamAppointmentService;
import com.gruppe10.submission.domain.Submission;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import jakarta.annotation.security.RolesAllowed;

@Route(value = "exam-correction", layout = MainLayout.class)
@RouteAlias(value = "exam-correction/:appointmentId?")
@PageTitle("Prüfungskorrektur")
@RolesAllowed("INSTRUCTOR")
public class ExamCorrectionView extends VerticalLayout implements HasUrlParameter<Long> {

    private final Grid<StudentExamAppointment> submissionsGrid;
    private ExamAppointment examAppointment;
    private final ExamAppointmentService examAppointmentService;

    public ExamCorrectionView(ExamAppointmentService examAppointmentService) {
        this.examAppointmentService = examAppointmentService;
        this.submissionsGrid = new Grid<>();
        setSizeFull();
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter Long appointmentId) {
        if (appointmentId == null) {
            throw new NotFoundException("Keine Prüfungstermin-ID angegeben");
        }

        this.examAppointment = examAppointmentService.findById(appointmentId)
            .orElseThrow(() -> new NotFoundException("Prüfungstermin nicht gefunden"));

        // Header-Bereich
        H2 viewTitle = new H2("Prüfungskorrektur: " + examAppointment.getTitle());
        Span examInfo = new Span("Prüfung: " + examAppointment.getExam().getTitle());
        
        configureSubmissionsGrid();
        
        // Layout
        removeAll(); // Clear previous content
        add(
            viewTitle,
            examInfo,
            submissionsGrid
        );
        
        refreshGrid();
    }

    private void configureSubmissionsGrid() {
        submissionsGrid.addColumn(sea -> sea.getVorname() + " " + sea.getNachname())
            .setHeader("Name")
            .setSortable(true);
            
        submissionsGrid.addColumn(StudentExamAppointment::getMatrikelnummer)
            .setHeader("Matrikelnummer")
            .setSortable(true);

        //Wenn bei der zugehörigen Submission getPassed null ist, soll es als nicht korriegiert angezeigt werden
        submissionsGrid.addColumn(sea -> {
            Submission sub = sea.getSubmission();
            return sub != null ? (sub.getPassed() != null ?
                   (sub.getPassed() ? "Bestanden" : "Nicht bestanden") : "Noch nicht korrigiert") : "Keine Abgabe";
        }).setHeader("Status");

        // Komponente für Punktzahl/Note (falls vorhanden)
        submissionsGrid.addColumn(sea -> {
            Submission sub = sea.getSubmission();
            return sub != null ? sub.getPassed() : "-";
        }).setHeader("Punkte");

        // Action-Buttons
        submissionsGrid.addComponentColumn(sea -> {
            Button korrigierenButton = new Button("Korrigieren",
                e -> navigateToSubmissionDetail(sea));
            return korrigierenButton;
        }).setHeader("Aktionen");

        submissionsGrid.setSizeFull();
    }

//    private Component createFilterSection() {
//        // Filter-Bereich
//        ComboBox<String> statusFilter = new ComboBox<>("Status Filter");
//        statusFilter.setItems("Alle", "Korrigiert", "Nicht korrigiert", "Keine Abgabe");
//        statusFilter.setValue("Alle");
//        statusFilter.addValueChangeListener(e -> applyFilters());
//
//        TextField nameFilter = new TextField("Name/Matrikelnummer");
//        nameFilter.setPlaceholder("Suchen...");
//        nameFilter.addValueChangeListener(e -> applyFilters());
//
//        HorizontalLayout filterLayout = new HorizontalLayout(statusFilter, nameFilter);
//        filterLayout.setAlignItems(Alignment.BASELINE);
//
//        return filterLayout;
//    }

    private void navigateToSubmissionDetail(StudentExamAppointment sea) {
        if (sea.getSubmission() != null) {
            getUI().ifPresent(ui -> 
                ui.navigate("submission-correction/" + sea.getSubmission().getId()));
        } else {
            Notification.show("Keine Abgabe vorhanden",
                3000, Notification.Position.MIDDLE);
        }
    }

    private void refreshGrid() {
        submissionsGrid.setItems(examAppointment.getStudentExamAppointments());
    }
}