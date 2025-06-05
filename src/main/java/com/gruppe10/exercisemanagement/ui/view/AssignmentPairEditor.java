package com.gruppe10.exercisemanagement.ui.view;

import com.gruppe10.exercisemanagement.domain.AssignmentPair;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import java.util.function.Consumer;


class AssignmentPairEditor extends FormLayout {
    private final Binder<AssignmentPair> binder = new Binder<>(AssignmentPair.class);
    private final TextField partOneField = new TextField("Teil A");
    private final TextField partTwoField = new TextField("Teil B");
    private final Button deleteButton = new Button(new Icon(VaadinIcon.CLOSE));
    private Consumer<Boolean> onValueChangeCallback;

    public AssignmentPairEditor() {
        setupUI();
        setupBinder();
    }

    private void setupBinder() {
        binder.forField(partOneField)
                .asRequired("Teil A erforderlich")
                .bind(AssignmentPair::getPartOne, AssignmentPair::setPartOne);

        binder.forField(partTwoField)
                .asRequired("Teil B erforderlich")
                .bind(AssignmentPair::getPartTwo, AssignmentPair::setPartTwo);
    }

    public boolean isValid() {
        return binder.validate().isOk();
    }

    public void setAssignmentPair(AssignmentPair pair) {
        binder.readBean(pair);
    }

    public AssignmentPair getAssignmentPair() {
//        AssignmentPair pair = new AssignmentPair();
//        binder.writeBeanIfValid(pair);
//        return pair;
        AssignmentPair pair = binder.getBean();
        if (pair == null) {
            pair = new AssignmentPair();
        }
        binder.writeBeanIfValid(pair);
        return pair;
    }

    public void setOnDelete(Runnable onDelete) {
        deleteButton.addClickListener(e -> onDelete.run());
    }

    private void setupUI() {
        setResponsiveSteps(new ResponsiveStep("0", 3));

        partOneField.setPlaceholder("Teil A");
        partTwoField.setPlaceholder("Teil B");
        partOneField.setWidthFull();
        partTwoField.setWidthFull();

        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
        deleteButton.setTooltipText("Dieses Zuordnungspaar löschen");
        deleteButton.getStyle().set("margin-top", "auto");
        deleteButton.getStyle().set("margin-bottom", "8px");

        partOneField.getStyle().set("flex", "0 1 45%");
        partTwoField.getStyle().set("flex", "0 1 45%");
        deleteButton.getStyle().set("flex", "0 1 5%");

        add(partOneField, partTwoField, deleteButton);
        setWidthFull();
    }

    public String getPartOne() {
        return partOneField.getValue();
    }

    public String getPartTwo() {
        return partTwoField.getValue();
    }

}