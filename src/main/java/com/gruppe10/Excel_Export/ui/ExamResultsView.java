package com.gruppe10.Excel_Export.ui;

import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.ChartType;
import com.vaadin.flow.component.charts.model.Configuration;
import com.vaadin.flow.component.charts.model.DataSeries;
import com.vaadin.flow.component.charts.model.DataSeriesItem;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.router.Route;
import com.gruppe10.Excel_Export.data.ExamResultDTO;
import com.gruppe10.exam.service.ExamService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import static com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment.*;
import java.util.List;


/**
 * ExamResultsView.java
 * <p>
 * Created by Fabian Holtapel on 13.05.2025.
 * <p>
 * Description:
 * TODO: Beschreibung einfügen.
 */



@Route("pruefungsergebnisse")
@RolesAllowed("STUDENT")
//@Theme(variant = Lumo.LIGHT)
public class ExamResultsView extends VerticalLayout{

    private final ExamService examService;

    @Autowired
    public ExamResultsView(ExamService examService) {
        this.examService = examService;

        addClassName(LumoUtility.Padding.LARGE);
        setWidthFull();
        setSpacing(true);

        H2 title = new H2("Meine Prüfungsergebnisse");
        title.addClassNames(LumoUtility.FontSize.XLARGE, LumoUtility.Margin.Bottom.MEDIUM);
        add(title);


        List<ExamResultDTO> results = examService.getExamResults();

        Grid<ExamResultDTO> grid = new Grid<>(ExamResultDTO.class, false);
        grid.setItems(results);
        grid.addColumn(ExamResultDTO::getTeilnehmer).setHeader("Teilnehmer").setAutoWidth(true);
        grid.addColumn(ExamResultDTO::getEmail).setHeader("E-Mail").setAutoWidth(true);
        grid.addColumn(ExamResultDTO::getGesamtpunkte).setHeader("Gesamtpunkte");
        grid.addColumn(r -> r.isBestanden() ? "✔" : "✖").setHeader("Bestanden");

        grid.addClassNames(LumoUtility.BoxShadow.SMALL);
        grid.setWidthFull();
        add(grid);

        Button exportButton = new Button("Ergebnisse als Excel herunterladen", event -> {
            getUI().ifPresent(ui -> ui.getPage().open("/api/exams/export", "_blank"));
        });
        exportButton.getStyle().set("margin-top", "1rem");
        add(exportButton);

        long bestanden = results.stream().filter(ExamResultDTO::isBestanden).count();
        long nichtBestanden = results.size() - bestanden;

        Chart pieChart = new Chart(ChartType.PIE);
        Configuration conf = pieChart.getConfiguration();
        conf.setTitle("Verteilung der Bestehensquote");

        DataSeries series = new DataSeries();
        series.add(new DataSeriesItem("Bestanden", bestanden));
        series.add(new DataSeriesItem("Nicht Bestanden", nichtBestanden));
        conf.setSeries(series);


        Div chartWrapper = new Div(pieChart);
        chartWrapper.getStyle().set("max-width", "400px").set("margin-top", "2rem");
        add(chartWrapper);

    }
}


