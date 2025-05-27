package com.gruppe10.Excel_Export.service;


import com.gruppe10.submission.domain.Submission;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ExcelExportUtil.java
 * <p>
 * Created by Fabian Holtapel on 18.05.2025.
 * <p>
 * Description:
 * TODO: Beschreibung einfügen.
 */

public class ExcelExportUtil {
    public static byte[] generateExcelInsturctor(List<Submission> submissions) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Ergebnisse");
            Row header = sheet.createRow(0);

            header.createCell(0).setCellValue("Teilnehmer");
            header.createCell(1).setCellValue("E-Mail");
            header.createCell(2).setCellValue("Gesamtpunkte");
            header.createCell(3).setCellValue("Bestanden");

            int rowIdx = 1;
            for (Submission sub : submissions) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(sub.getStudent().getForename() + " " + sub.getStudent().getSurname());
                row.createCell(1).setCellValue(sub.getStudent().getEmail());
                row.createCell(2).setCellValue(sub.getTotalPoints());
                row.createCell(3).setCellValue(sub.getPassed() ? "Ja" : "Nein");
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }
    public static byte[] generateExcelStudent(List<Submission> submissions) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Meine Ergebnisse");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Prüfungstitel");
            header.createCell(1).setCellValue("Datum");
            header.createCell(2).setCellValue("Gesamtpunkte");
            header.createCell(3).setCellValue("Bestanden");

            Set<String> allAufgaben = submissions.stream()
                    .flatMap(s -> s.getAufgabenErgebnisse().keySet().stream())
                    .collect(Collectors.toCollection(TreeSet::new));

            int baseCols = 4;
            int idx = baseCols;
            List<String> aufgabenList = new ArrayList<>(allAufgaben);
            for (String aufgabe : aufgabenList) {
                header.createCell(idx++).setCellValue(aufgabe);
            }

            int rowIdx = 1;
            for (Submission s : submissions) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(s.getExam().getTitle());
                row.createCell(1).setCellValue(s.getExam().getCreationDate().toString());
                row.createCell(2).setCellValue(s.getTotalPoints());
                row.createCell(3).setCellValue(s.getPassed() ? "Ja" : "Nein");

                Map<String, Double> aufgabenMap = s.getAufgabenErgebnisse();
                for (int i = 0; i < aufgabenList.size(); i++) {
                    Double punkte = aufgabenMap.getOrDefault(aufgabenList.get(i), 0.0);
                    row.createCell(baseCols + i).setCellValue(punkte);
                }
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }
}
