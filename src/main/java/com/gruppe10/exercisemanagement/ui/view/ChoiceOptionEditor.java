package com.gruppe10.exercisemanagement.ui.view;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.TextField;


class ChoiceOptionEditor extends Div {
    private final TextField answerTextField = new TextField("Antwortmöglichkeit eingeben...");
    private final Checkbox correctCheckbox = new Checkbox("Als richtig markieren");

    public ChoiceOptionEditor() {
        add(answerTextField, correctCheckbox);
    }

    public String getAnswerText() {
        return answerTextField.getValue();
    }

    public boolean isCorrect() {
        return correctCheckbox.getValue();
    }
}
