/**
 * Author: Christian Markow
 * Date: 04.06.2025
 */

package com.gruppe10.examManagement.exam.ui.ListView;

import com.gruppe10.base.ui.Layout.MainLayout;
import com.gruppe10.examManagement.exam.domain.Exam;
import com.gruppe10.examManagement.exam.service.ExamService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import jakarta.annotation.security.RolesAllowed;

import java.time.Clock;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Locale;

@Route(value = "student-pruefung-list", layout = MainLayout.class)
@PageTitle("Offene Prüfungen")
@RolesAllowed("STUDENT")
public class StudentExamListView extends VerticalLayout {

    private final ExamService examService;
    private final Grid<Exam> examGrid;

    public StudentExamListView(ExamService examService, Clock clock) {
        this.examService = examService;

        setSizeFull();
        addClassNames(LumoUtility.BoxSizing.BORDER, LumoUtility.Display.FLEX, LumoUtility.FlexDirection.COLUMN,
                LumoUtility.Padding.MEDIUM, LumoUtility.Gap.SMALL);

        add(new H2("Offene Prüfungen"));

        examGrid = new Grid<>(Exam.class, false);
        examGrid.setSizeFull();

        var dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                .withLocale(Locale.getDefault())
                .withZone(clock.getZone());

        examGrid.addColumn(Exam::getId).setHeader("Id").setAutoWidth(true);
        examGrid.addColumn(Exam::getTitle).setHeader("Titel").setAutoWidth(true);
        examGrid.addColumn(pruefung -> dateTimeFormatter.format(pruefung.getCreationDate()))
                .setHeader("Erstellt am").setAutoWidth(true);
        examGrid.addColumn(Exam::getGesamtpunkte).setHeader("Gesamtpunkte").setAutoWidth(true);
        examGrid.addColumn(Exam::getBestehensgrenze).setHeader("Bestehensgrenze").setAutoWidth(true);

        examGrid.setItems(query ->
                listAllOpenExams()
                        .stream());

        examGrid.addItemDoubleClickListener(event -> {
            if (event.getItem() != null) {
                UI.getCurrent().navigate("exam/" + event.getItem().getId());
            }
        });

        add(examGrid);
    }

    public List<Exam> listAllOpenExams() {
        return examService.findByGesperrtFalse();
    }

}