package com.gruppe10.examAppointment.ui;



import com.gruppe10.examAppointment.domain.StudentData;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;

import java.util.ArrayList;
import java.util.List;

/**
 * Diese KLasse zeigt einen Dialog um eine Exceldatei einzulesen
 * Es gibt noch 2 Methoden um die Exceldatei zu verarbeiten und in eine Liste an StudentData-Objekten zu packen
 * **/
public class ExcelImportDialog extends Dialog {
    private final MemoryBuffer buffer;
    private final Upload upload;
    private final ImportCallback callback;

    public interface ImportCallback {
        void onImportSuccess(List<StudentData> studentData);
    }

    //Konstruktor der Klasse und des Dialoglayouts
    public ExcelImportDialog(ImportCallback callback) {
        this.callback = callback;

        setWidth("500px");

        VerticalLayout layout = new VerticalLayout();
        layout.add(new H3("Prüflinge importieren"));

        buffer = new MemoryBuffer();
        upload = new Upload(buffer);
        upload.setAcceptedFileTypes(".xlsx", ".xls");

        upload.addSucceededListener(event -> {
            try {
                processExcelFile();
                close();
            } catch (Exception e) {
                com.vaadin.flow.component.notification.Notification
                        .show("Fehler beim Import: " + e.getMessage(),
                                3000,
                                com.vaadin.flow.component.notification.Notification.Position.MIDDLE);
            }
        });

        layout.add(upload);
        add(layout);
    }

    //Methode zum verarbeiten der Exceldatei und in eine Liste an StudentData-Objekten umzuwandeln
    private void processExcelFile() throws Exception {
        List<StudentData> studentData = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(buffer.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Überspringen der Kopfzeile

                String nachname = getCellValueAsString(row.getCell(0));
                String vorname = getCellValueAsString(row.getCell(1));
                String matrikelnummer = getCellValueAsString(row.getCell(2));

                if (nachname != null && !nachname.isEmpty()) {
                    studentData.add(new StudentData(nachname, vorname, matrikelnummer));
                }
            }
        }

        if (studentData.isEmpty()) {
            throw new Exception("Keine Prüflinge in der Excel-Datei gefunden");
        }

        callback.onImportSuccess(studentData);
    }

    //Hilfsmethode zum Lesen von Zellenwerten als String
    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue().trim();
            case NUMERIC: return String.valueOf((long) cell.getNumericCellValue());
            default: return "";
        }
    }

}