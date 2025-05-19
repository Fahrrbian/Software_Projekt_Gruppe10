package com.gruppe10.exercisemanagement.ui.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;


class AssignmentPairEditor extends Div {
    private final TextField partOneField = new TextField("Teil A");
    private final TextField partTwoField = new TextField("Teil B");
    private final Button deleteButton = new Button(new Icon(VaadinIcon.CLOSE));

    public AssignmentPairEditor() {
        //add(partOneField, partTwoField);
        setupUI();
    }

    public void setOnDelete(Runnable onDelete) {
        deleteButton.addClickListener(e -> onDelete.run());
    }

    private void setupUI() {
        HorizontalLayout layout = new HorizontalLayout(partOneField, partTwoField, deleteButton);
        layout.setDefaultVerticalComponentAlignment(Alignment.END);
        layout.setFlexGrow(1, partOneField);
        layout.setFlexGrow(1, partTwoField);

        partOneField.setWidthFull();
        partOneField.getStyle().set("min-width", "300px");

        partTwoField.setWidthFull();
        partTwoField.getStyle().set("min-width", "300px");

        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
        deleteButton.setTooltipText("Dieses Zuordnungspaar löschen");
        deleteButton.getStyle().set("margin-bottom", "6px");

        add(layout);
    }

    public String getPartOne() {
        return partOneField.getValue();
    }

    public String getPartTwo() {
        return partTwoField.getValue();
    }
}