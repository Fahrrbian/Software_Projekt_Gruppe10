package com.gruppe10.Excel_Export.ui;

import com.gruppe10.base.ui.Layout.TimedMainLayout;
import com.gruppe10.base.ui.security.SecurityUtils;
import com.gruppe10.submission.domain.Submission;
import com.gruppe10.submission.service.SubmissionService;
import com.gruppe10.usermanagement.domain.Student;
import com.gruppe10.usermanagement.domain.User;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

/**
 * ExamResultsView.java
 * <p>
 * Created by Fabian Holtapel on 13.05.2025.
 * <p>
 * Description:
 * TODO: Beschreibung einfügen.
 */

@Route(value = "pruefungsergebnisse", layout = TimedMainLayout.class)
@RolesAllowed("STUDENT")
//@Theme(variant = Lumo.LIGHT)
public class ExamResultsView extends VerticalLayout{

    private final SubmissionService submissionService;

    @Autowired
    public ExamResultsView(SubmissionService submissionService) {

        setSizeFull();
        setPadding(false);
        setSpacing(false);

        this.submissionService = submissionService;

        H2 title = new H2("Meine Prüfungsergebnisse");
        title.getStyle().set("margin-bottom", "var(--lumo-space-m)");
        User currentUser = (User) SecurityUtils.getCurrentUser().orElse(null);
        if (currentUser instanceof Student student) {
            System.out.println("-------------------->currentUser = " + currentUser);
            List<Submission> submissions = submissionService.getSubmissionsByStudent(student);

            Grid<Submission> grid = new Grid<>(Submission.class, false);
            grid.addColumn(sub -> sub.getExam().getTitle()).setHeader("Prüfung");
            grid.addColumn(Submission::getTotalPoints).setHeader("Gesamtpunkte");
            grid.addColumn(sub -> {
                Boolean passed = sub.getPassed();
                if (passed == null) {
                    return "N/A";
                }
                return passed ? "✔" : "✖";
            }).setHeader("Bestanden");

            grid.setItems(submissionService.getSubmissionsByStudent(
                    (Student) SecurityUtils.getCurrentUser().get()));
            grid.setSizeFull();                                      // füllt die Höhe des Eltern-Layouts
            grid.addThemeVariants(                                  // optische Verbesserungen
                    GridVariant.LUMO_ROW_STRIPES,
                    GridVariant.LUMO_COLUMN_BORDERS
            );


            Button export = new Button("Export als Excel", e ->
                    getUI().ifPresent(ui -> ui.getPage().open("/api/student/export", "_blank"))
            );
            export.getStyle().set("margin-top", "var(--lumo-space-m)");

            add(title, grid, export);
            setFlexGrow(1, grid);
        }
    }
}

