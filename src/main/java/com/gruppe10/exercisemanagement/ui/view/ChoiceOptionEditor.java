package com.gruppe10.exercisemanagement.ui.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;


class ChoiceOptionEditor extends Div {
    private final TextField answerTextField = new TextField("Antwortmöglichkeit eingeben");
    private final Checkbox correctCheckbox = new Checkbox("Als richtig markieren");
    private final Button deleteButton = new Button(new Icon(VaadinIcon.CLOSE));

    public ChoiceOptionEditor() {
        //add(answerTextField, correctCheckbox);
        setupUI();
    }

    public void setOnDelete(Runnable onDelete) {
        deleteButton.addClickListener(e -> onDelete.run());
    }

    private void setupUI() {
        HorizontalLayout layout = new HorizontalLayout(answerTextField, correctCheckbox, deleteButton);
        layout.setDefaultVerticalComponentAlignment(Alignment.END);
        layout.setFlexGrow(1, answerTextField);

        answerTextField.setWidthFull();
        answerTextField.getStyle().set("min-width", "300px");

        correctCheckbox.getStyle().set("margin-bottom", "8px");
        deleteButton.getStyle().set("margin-bottom", "8px");

        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
        deleteButton.setTooltipText("Diese Antwortmöglichkeit löschen");

        add(layout);
    }

    public String getAnswerText() {
        return answerTextField.getValue();
    }

    public boolean isCorrect() {
        return correctCheckbox.getValue();
    }
}
