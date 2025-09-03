package com.gruppe10.Excel_Export.ui;

import com.gruppe10.submission.DTOs.ReviewDto;
import com.gruppe10.submission.DTOs.SubmissionDto;
import com.gruppe10.submission.domain.SubmissionStatus;
import com.gruppe10.submission.ui.SubmissionUIService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.dialog.Dialog;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * ReviewDialog.java
 * <p>
 * Created by Fabian Holtapel on 03.06.2025.
 * <p>
 * Description:
 * TODO: Beschreibung einfügen.
 */

public class ReviewDialog extends Dialog {

    public ReviewDialog(SubmissionDto sub, Consumer<SubmissionDto> onSave) {
        setWidth("700px");
        setHeight("500px");

        FormLayout form = new FormLayout();
        form.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1));

        form.addFormItem(new Div(), "Student: " + sub.getEmail());
        form.addFormItem(new Div(), "Status: " + sub.getStatus());

        Map<String, String> rawAnswers = sub.getRawAnswers();
        Map<String, Double> perPoints = sub.getPerQuestionPoints();
        Map<String, NumberField> pointFields = new HashMap<>();

        for (String qid : rawAnswers.keySet()) {
            String raw = rawAnswers.get(qid);
            Double auto = perPoints.getOrDefault(qid, 0.0);

            Div rawDiv = new Div();
            rawDiv.setText("Frage " + qid + " – Antwort: " + raw);

            NumberField ptsField = new NumberField();
            ptsField.setLabel("Punkte (automatisch: " + auto + ")");
            ptsField.setValue(auto);
            ptsField.setMin(0);
            pointFields.put(qid, ptsField);

            form.addFormItem(rawDiv, "");
            form.addFormItem(ptsField, "");
        }

        Button save = new Button("Speichern & Freigeben", evt -> {
            Map<String, Double> updatedPoints = new HashMap<>();
            pointFields.forEach((qid, fld) -> updatedPoints.put(qid, fld.getValue() != null ? fld.getValue() : 0.0));

            sub.setPerQuestionPoints(updatedPoints);

            onSave.accept(sub);

            Notification.show("Punkte gespeichert und Freigabe erteilt", 2000, Notification.Position.MIDDLE);
            sub.setStatus(SubmissionStatus.REVIEWED);
            close();
        });

        Button cancel = new Button("Abbrechen", evt -> close());

        HorizontalLayout buttons = new HorizontalLayout(save, cancel);
        form.add(buttons);

        add(form);
    }
}
