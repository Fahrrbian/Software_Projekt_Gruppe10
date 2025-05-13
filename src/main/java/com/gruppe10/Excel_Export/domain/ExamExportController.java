package com.gruppe10.Excel_Export.domain;

import com.gruppe10.exam.service.ExamService;
import com.gruppe10.Excel_Export.data.ExamResultDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * ExamExportController.java
 * <p>
 * Created by Fabian Holtapel on 13.05.2025.
 * <p>
 * Description:
 * TODO: Beschreibung einfügen.
 */
@RestController
@RequestMapping("/api/exams")
public class ExamExportController {

    @Autowired
    private ExamService examService;

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportExamResultsToExcel() throws IOException {

        List<ExamResultDTO> results = examService.getExamResults();

        if (results == null || results.isEmpty()) {
            System.out.println("No exam results found");
            return ResponseEntity.noContent().build();
        }

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Ergebnisse");
            Row header = sheet.createRow(0);

            Set<String> dynamicColumns = new TreeSet<>();
            for (ExamResultDTO result : results) {
                dynamicColumns.addAll(result.getAufgabenErgebnisse().keySet());
            }

            List<String> orderedColumns = new ArrayList<>(dynamicColumns);

            int colIdx = 0;
            header.createCell(colIdx++).setCellValue("Teilnehmer");
            header.createCell(colIdx++).setCellValue("E-Mail");
            for (String aufgabe : orderedColumns) {
                header.createCell(colIdx++).setCellValue(aufgabe);
            }
            header.createCell(colIdx++).setCellValue("Gesamtpunkte");
            header.createCell(colIdx).setCellValue("Bestanden");

            int rowIdx = 1;
            for (ExamResultDTO result : results) {
                Row row = sheet.createRow(rowIdx++);
                int i = 0;
                row.createCell(i++).setCellValue(result.getTeilnehmer());
                row.createCell(i++).setCellValue(result.getEmail());
                for (String aufgabe : orderedColumns) {
                    Double punkte = result.getAufgabenErgebnisse().getOrDefault(aufgabe, 0.0);
                    row.createCell(i++).setCellValue(punkte);
                }
                row.createCell(i++).setCellValue(result.getGesamtpunkte());
                row.createCell(i).setCellValue(result.isBestanden() ? "Ja" : "Nein");
            }

            workbook.write(out);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=exam-results.xlsx")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(out.toByteArray());
        }
    }
}
