package com.gruppe10.exercisemanagement.ui.view;

import com.gruppe10.exercisemanagement.domain.ChoiceOption;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;


class ChoiceOptionEditor extends FormLayout {
    private final TextField answerTextField = new TextField("Antwortmöglichkeit eingeben");
    private final Checkbox correctCheckbox = new Checkbox("Richtig");
    private final Button deleteButton = new Button(new Icon(VaadinIcon.CLOSE));

    public ChoiceOptionEditor() {
        setupUI();
    }

    public void setOnDelete(Runnable onDelete) {
        deleteButton.addClickListener(e -> onDelete.run());
    }

    private void setupUI() {
//        setWidthFull();
//        setAlignItems(Alignment.END);
//        setSpacing(false);
//        setPadding(false);
//        setMargin(false);
//
//        answerTextField.setWidth("75%");
//        answerTextField.getStyle().set("min-width", "100px");
//
//        correctCheckbox.setWidth("20%");
//        correctCheckbox.getStyle().set("margin-bottom", "8px");
//
//        deleteButton.setWidth("5%");
//        deleteButton.getStyle().set("margin-bottom", "8px");
//        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
//        deleteButton.setTooltipText("Diese Antwortmöglichkeit löschen");
//
//        add(answerTextField, correctCheckbox, deleteButton);
//        setResponsiveSteps(
//                new ResponsiveStep("0", 1),
//                new ResponsiveStep("500px", 3) // 3 Spalten: Text, Checkbox, Button
//        );
//
//        answerTextField.setWidthFull();
//        correctCheckbox.getStyle().set("margin-top", "30px"); // optisch mittig
//        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
//        deleteButton.setTooltipText("Diese Option löschen");
//        deleteButton.getStyle().set("margin-top", "30px");
//
//        // Reihenfolge: Textfeld | Checkbox | Löschen-Button
//        addFormItem(answerTextField, "Antwortoption");
//        add(correctCheckbox);
//        add(deleteButton);
//
//        setWidthFull();
        setResponsiveSteps(new ResponsiveStep("0", 3));
        setColspan(answerTextField, 1);
        setColspan(correctCheckbox, 1);
        setColspan(deleteButton, 1);

        answerTextField.setPlaceholder("Antwortoption");
        answerTextField.setWidthFull();
        correctCheckbox.getStyle().set("margin-top", "auto");
        correctCheckbox.getStyle().set("margin-bottom", "8px");
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
        deleteButton.setTooltipText("Diese Option löschen");
        deleteButton.getStyle().set("margin-top", "auto");
        deleteButton.getStyle().set("margin-bottom", "8px");

        answerTextField.getStyle().set("flex", "0 1 80%");
        correctCheckbox.getStyle().set("flex", "0 1 10%");
        deleteButton.getStyle().set("flex", "0 1 5%");

        add(answerTextField, correctCheckbox, deleteButton);
        setWidthFull();
    }

    public String getAnswerText() {
        return answerTextField.getValue();
    }

    public boolean isCorrect() {
        return correctCheckbox.getValue();
    }

    public ChoiceOption getChoiceOption() {
        return new ChoiceOption(answerTextField.getValue(), correctCheckbox.getValue(), null);
    }
}
