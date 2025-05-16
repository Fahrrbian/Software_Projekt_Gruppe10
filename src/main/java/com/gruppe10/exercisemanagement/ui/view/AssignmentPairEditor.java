package com.gruppe10.exercisemanagement.ui.view;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.TextField;

class AssignmentPairEditor extends Div {
    private final TextField partOneField = new TextField("Teil A");
    private final TextField partTwoField = new TextField("Teil B");

    public AssignmentPairEditor() {
        add(partOneField, partTwoField);
    }

    public String getPartOne() {
        return partOneField.getValue();
    }

    public String getPartTwo() {
        return partTwoField.getValue();
    }
}