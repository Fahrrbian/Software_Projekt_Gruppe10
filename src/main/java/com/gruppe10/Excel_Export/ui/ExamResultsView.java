package com.gruppe10.Excel_Export.ui;

import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.ChartType;
import com.vaadin.flow.component.charts.model.Configuration;
import com.vaadin.flow.component.charts.model.DataSeries;
import com.vaadin.flow.component.charts.model.DataSeriesItem;
import com.vaadin.flow.router.Route;
import com.gruppe10.Excel_Export.data.ExamResultDTO;
import com.gruppe10.exam.service.ExamService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
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
public class ExamResultsView extends VerticalLayout{

    private final ExamService examService;

    @Autowired
    public ExamResultsView(ExamService examService) {
        this.examService = examService;

        List<ExamResultDTO> results = examService.getExamResults();

        Grid<ExamResultDTO> grid = new Grid<>(ExamResultDTO.class);
        grid.setItems(results);
        grid.setColumns("teilnehmer", "email", "gesamtpunkte", "bestanden");
        add(grid);

        Anchor downloadLink = new Anchor("/api/exams/export", "Ergebnisse als Excel herunterladen");
        downloadLink.getElement().setAttribute("download", true);
        add(downloadLink);

        long bestanden = results.stream().filter(ExamResultDTO::isBestanden).count();
        long nichtBestanden = results.size() - bestanden;

        Chart pieChart = new Chart(ChartType.PIE);
        Configuration conf = pieChart.getConfiguration();
        conf.setTitle("Verteilung der Bestehensquote");

        DataSeries series = new DataSeries();
        series.add(new DataSeriesItem("Bestanden", bestanden));
        series.add(new DataSeriesItem("Nicht Bestanden", nichtBestanden));

        conf.setSeries(series);
        add(pieChart);

    }
}


