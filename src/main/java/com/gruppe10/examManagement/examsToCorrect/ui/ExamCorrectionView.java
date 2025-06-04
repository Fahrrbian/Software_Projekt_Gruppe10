package com.gruppe10.examManagement.examsToCorrect.ui;

import com.gruppe10.base.ui.Layout.MainLayout;
import com.gruppe10.examManagement.exam.domain.Exam;
import com.gruppe10.examManagement.exam.service.ExamService;
import com.gruppe10.examManagement.examAppointment.domain.StudentExam;
import com.gruppe10.Excel_Export.ui.ReviewDialog;
import com.gruppe10.examManagement.examAppointment.domain.ExamAppointment;
import com.gruppe10.examManagement.examAppointment.domain.StudentExamAppointment;
import com.gruppe10.examManagement.examAppointment.service.ExamAppointmentService;
import com.gruppe10.submission.DTOs.SubmissionDto;
import com.gruppe10.submission.domain.Submission;
import com.gruppe10.submission.domain.SubmissionStatus;
import com.gruppe10.submission.ui.SubmissionUIService;
import com.vaadin.flow.component.button.Button;
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

    private final Grid<StudentExam> submissionsGrid;
    private Exam exam;
    private final ExamService examService;

    public ExamCorrectionView(ExamService examService) {
        this.examService = examService;
        this.submissionsGrid = new Grid<>();
        setSizeFull();
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter Long examId) {
        if (examId == null) {
            throw new NotFoundException("Keine Prüfungstermin-ID angegeben");
        }

        this.exam = examService.getById(examId)
            .orElseThrow(() -> new NotFoundException("Prüfungstermin nicht gefunden"));

        configureSubmissionsGrid();

        // Layout
        removeAll(); // Clear previous content
        add(
                new H2("Prüfungskorrektur: " + exam.getTitle()),
            submissionsGrid
        );
        
        refreshGrid();
    }

    private void configureSubmissionsGrid() {
        submissionsGrid.addColumn(sea -> sea.getVorname() + " " + sea.getNachname())
            .setHeader("Name")
            .setSortable(true);
            
        submissionsGrid.addColumn(StudentExam::getMatrikelnummer)
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
                    Submission submission = sea.getSubmission();

                    if (submission != null) {
                        Button korrigierenButton = new Button(
                                submission.getStatus() == SubmissionStatus.PENDING_REVIEW ? "Korrigieren" : "Ansehen",
                                e -> openReviewDialog(submission)
                        );
                        return korrigierenButton;
                    } else {
                        return new Span("Keine Abgabe");
                    }
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

    private void navigateToSubmissionDetail(StudentExam sea) {
        if (sea.getSubmission() != null) {
            getUI().ifPresent(ui -> 
                ui.navigate("submission-correction/" + sea.getSubmission().getId()));
        } else {
            Notification.show("Keine Abgabe vorhanden",
                3000, Notification.Position.MIDDLE);
        }
    }
    /*
    private void refreshGrid() {
        submissionsGrid.setItems(exam.getStudentExamAppointments());
    }
*/
    private void openReviewDialog(Submission submission) {
            // Optional: konvertiere Submission → SubmissionDto
            SubmissionDto dto = SubmissionDto.from(submission);
            ReviewDialog dialog = new ReviewDialog(dto, new SubmissionUIService()); // ggf. via Konstruktor übergeben
            dialog.open();
            dialog.addOpenedChangeListener(e -> {
                if (!e.isOpened()) {
                    refreshGrid();
                }
            });
        }
        private void refreshGrid() {
            this.examAppointment = examAppointmentService.findById(examAppointment.getId())
                    .orElseThrow(() -> new NotFoundException("Termin nicht gefunden"));
            submissionsGrid.setItems(examAppointment.getStudentExamAppointments());
        }
}

