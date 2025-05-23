package com.gruppe10.examManagement.examAppointment.ui;

import com.gruppe10.examManagement.exam.domain.Exam;
import com.gruppe10.examManagement.exam.service.ExamService;
import com.gruppe10.examManagement.examAppointment.domain.ExamAppointment;
import com.gruppe10.examManagement.examAppointment.service.ExamAppointmentService;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import org.springframework.data.domain.Pageable;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

/**
 *Diese Klasse ist das Formular um sich einen Prüfungstermin anzeigen zu lassen
 * **/


public class ExamAppointmentForm extends FormLayout {
    private final ExamAppointment appointment;
    private final TextField title;
    private final ComboBox<Exam> examSelect;
    private final DateTimePicker appointmentDate;

    public ExamAppointmentForm(ExamAppointmentService appointmentService,
                               ExamService examService,
                               ExamAppointment appointment) {
        this.appointment = appointment;

        title = new TextField("Titel");
        title.setValue(appointment.getTitle() != null ? appointment.getTitle() : "");
        title.setRequired(true);

        examSelect = new ComboBox<>("Prüfung");
        List<Exam> exams = examService.list(Pageable.unpaged());
        examSelect.setItems(exams);
        examSelect.setItemLabelGenerator(Exam::getTitle);
        examSelect.setRequired(true);

        //Wenn vorhanden, Exam in der liste suchen, um Werte zu setzen
        if (appointment.getExam() != null) {
            exams.stream()
                    .filter(e -> e.getId().equals(appointment.getExam().getId()))
                    .findFirst()
                    .ifPresent(examSelect::setValue);
            examSelect.setReadOnly(true);
        }


        // Setzen des Datums, wenn vorhanden
        appointmentDate = new DateTimePicker("Termin");
        appointmentDate.setMin(LocalDateTime.now());
        appointmentDate.setStep(Duration.ofMinutes(15));
        if (appointment != null && appointment.getAppointmentDate() != null) {
            appointmentDate.setValue(LocalDateTime.ofInstant(
                    appointment.getAppointmentDate(),
                    ZoneId.systemDefault()
            ));
        }

        // Actionlistener für Änderungen der Prüfungsauswahl in der ComboBox
        // Nach einmaliger Auswahl soll das auf readOnly gesetzt werden
        examSelect.addValueChangeListener(event -> {
            if (event.getValue() != null) {
                examSelect.setReadOnly(true);

            }
        });



        add(title, examSelect, appointmentDate);
    }

    public String getTitle() {
        return title.getValue();
    }

    public java.time.Instant getAppointmentDate() {
        return appointmentDate.getValue()
                .atZone(ZoneId.systemDefault())
                .toInstant();
    }

    public Long getSelectedExamId() {
        return examSelect.getValue() != null ? examSelect.getValue().getId() : null;
    }

    public ComboBox<Exam> getExamSelect() {
        return examSelect;
    }

    public void resetForm() {
        examSelect.setReadOnly(false);
        examSelect.clear();
        title.clear();
        appointmentDate.clear();
    }




}
