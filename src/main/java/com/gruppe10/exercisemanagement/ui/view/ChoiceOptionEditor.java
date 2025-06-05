package com.gruppe10.exercisemanagement.ui.view;

import com.gruppe10.exercisemanagement.domain.ChoiceOption;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;


class ChoiceOptionEditor extends FormLayout {
    private final Binder<ChoiceOption> binder = new Binder<>(ChoiceOption.class);
    private final TextField answerTextField = new TextField("Antwortmöglichkeit eingeben");
    private final Checkbox correctCheckbox = new Checkbox("Richtig");
    private final Button deleteButton = new Button(new Icon(VaadinIcon.CLOSE));

    public ChoiceOptionEditor() {
        setupUI();
        setupBinder();
    }

    private void setupBinder() {
        binder.forField(answerTextField)
                .asRequired("Antworttext erforderlich")
                .bind(ChoiceOption::getText, ChoiceOption::setText);

        binder.forField(correctCheckbox)
                .bind(ChoiceOption::isCorrect, ChoiceOption::setCorrect);
    }

    public boolean isValid() {
        return binder.validate().isOk();
    }

    public ChoiceOption getChoiceOption() {
//        ChoiceOption option = new ChoiceOption();
//        binder.writeBeanIfValid(option);
//        return option;
        ChoiceOption option = binder.getBean();
        if (option == null) {
            option = new ChoiceOption();
        }
        binder.writeBeanIfValid(option);
        return option;
    }

    public void setChoiceOption(ChoiceOption option) {
        binder.readBean(option);
    }

    public void setOnDelete(Runnable onDelete) {
        deleteButton.addClickListener(e -> onDelete.run());
    }

    private void setupUI() {
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
        correctCheckbox.getStyle().set("flex", "0 1 5%");
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

}
