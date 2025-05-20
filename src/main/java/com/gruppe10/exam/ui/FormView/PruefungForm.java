package com.gruppe10.exam.ui.FormView;

/**
 * Author: Henrik Struckmeier
 * Date: 02/05/2025
 **/


import com.gruppe10.exam.domain.Exam;
import com.gruppe10.exam.service.ExamService;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.theme.lumo.LumoUtility;


public class PruefungForm extends FormLayout {

    Exam IExamInterface;
    private TextField title;
    private TextField bestehensgrenze;
    private TextField gesamtpunkte;
    private final ExamService examService;

    public PruefungForm(ExamService examService, Exam paramIExamInterface) {
        this.examService = examService;
        IExamInterface = paramIExamInterface;
        title = new TextField("Titel");
        title.setValue(IExamInterface.getTitle());
        bestehensgrenze = new TextField("Bestehensgrenze");
        bestehensgrenze.setValue(Double.toString(IExamInterface.getBestehensgrenze()));
        gesamtpunkte = new TextField("Gesamtpunkte");
        gesamtpunkte.setValue(Double.toString(IExamInterface.getGesamtpunkte()));
        add(title, bestehensgrenze, gesamtpunkte);

        setSizeFull();
        addClassNames(LumoUtility.BoxSizing.BORDER, LumoUtility.Display.FLEX, LumoUtility.FlexDirection.COLUMN,
                LumoUtility.Padding.MEDIUM, LumoUtility.Gap.SMALL);

    }

    //Getter für Felder folgen
    public String getTitle() {
        return title.getValue();
    }

    public double getBestehensgrenze() {
        try {
            return Double.parseDouble(bestehensgrenze.getValue());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    public double getGesamtpunkte() {
        try {
            return Double.parseDouble(gesamtpunkte.getValue());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}



