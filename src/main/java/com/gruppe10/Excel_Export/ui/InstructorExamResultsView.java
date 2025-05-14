package com.gruppe10.Excel_Export.ui;

import com.gruppe10.exam.service.ExamService;
import com.gruppe10.exam.domain.Exam;
import com.gruppe10.submission.domain.Submission;
import com.gruppe10.submission.service.SubmissionService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.Lumo;
import com.vaadin.flow.theme.Theme;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
/**
 * InstructorExamResultsView.java
 * <p>
 * Created by Fabian Holtapel on 13.05.2025.
 * <p>
 * Description:
 * TODO: Beschreibung einfügen.
 */

/*
@Route("auswertung")
@RolesAllowed("INSTRUCTOR")
public class InstructorExamResultsView extends VerticalLayout{

    private final ExamService examService;
    private final SubmissionService submissionService;

    private ComboBox<Exam> examSelector;
    private Grid<Submission> resultGrid;

    @Autowired
    public InstructorExamResultsView(ExamService examService, SubmissionService submissionService) {
        this.examService = examService;
        this.submissionService = submissionService;

        setSpacing(true);
        setPadding(true);

        add(new H2("Auswertung: Alle Prüfungsergebnisse"));

        examSelector = new ComboBox<>("Wähle eine Prüfung");
        examSelector.setItemLabelGenerator(Exam::getTitle);
        examSelector.setItems(examService.getExamsByCurrentInstructor());
        add(examSelector);

        resultGrid = new Grid<>(Submission.class, false);
        resultGrid.addColumn(sub -> sub.getStudent().getFirstname() + " " + sub.getStudent().getLastname())
                .setHeader("Teilnehmer");
        resultGrid.addColumn(sub -> sub.getStudent().getEmail()).setHeader("E-Mail");
        resultGrid.addColumn(Submission::getTotalPoints).setHeader("Gesamtpunkte");
        resultGrid.addColumn(sub -> sub.getPassed() ? "✔" : "✖").setHeader("Bestanden");

        add(resultGrid);

        examSelector.addValueChangeListener(event -> {
            Exam selected = event.getValue();
            if (selected != null) {
                List<Submission> submissions = submissionService.getSubmissionsByExam(selected);
                resultGrid.setItems(submissions);
            } else {
                resultGrid.setItems(List.of());
                Notification.show("Keine Prüfung ausgewählt.");
            }
        });

        Button exportButton = new Button("Excel Export dieser Prüfung", e -> {
            Exam selected = examSelector.getValue();
            if (selected != null) {
                getUI().ifPresent(ui -> ui.getPage().open("/api/exams/export/" + selected.getId(), "_blank"));
            } else {
                Notification.show("Bitte zuerst eine Prüfung auswählen.");
            }
        });
        add(exportButton);
    }
}
*/