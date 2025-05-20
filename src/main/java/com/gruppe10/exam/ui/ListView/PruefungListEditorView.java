package com.gruppe10.exam.ui.ListView;

/**
 * Author: Henrik Struckmeier
 * Date: 02/05/2025
 **/


import com.gruppe10.exam.domain.Exam;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;


public class PruefungListEditorView extends FormLayout {
    //Hier können Kompoenten des Fomulars erstellt werden und dem Array
    //zur Erstellung hinzugefügt werden
    TextField title = new TextField("Title");
    TextField bestehensgrenze = new TextField("Bestehensgrenze");
    Button save = new Button("Änderungen speichern", event -> pushUpdate());
    Button delete = new Button("Eintrag löschen");
    Component[] components = new Component[] {title, bestehensgrenze, save, delete};

    private final ExamListView pruefungListView;

    //Konstruktor
    public PruefungListEditorView(ExamListView pruefungListView) {
        addClassName("contact-form");
        this.pruefungListView = pruefungListView;
        //Komponenten hinzufügen
        for (int i = 0; i < components.length ; i++) {
            addComponentAtIndex(i, components[i]);
        }
    }

    private void pushUpdate() {
        Exam p = new Exam();
        p.setTitle(title.getValue());
        p.setBestehensgrenze(Double.parseDouble(bestehensgrenze.getValue()));
        pruefungListView.pushUpdate(p);
    }

    //Getter und Setter folgen
    public TextField getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title.setLabel("Titel");
        this.title.setValue(title);
    }

    public TextField getBestehensgrenze() {
        return bestehensgrenze;
    }

    public void setBestehensgrenze(String bestehensgrenze) {
        this.bestehensgrenze.setLabel("Bestehensgrenze");
        this.bestehensgrenze.setValue(bestehensgrenze);
    }
}
