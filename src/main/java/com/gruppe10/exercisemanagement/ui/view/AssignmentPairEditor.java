package com.gruppe10.exercisemanagement.ui.view;

import com.gruppe10.exercisemanagement.domain.AssignmentPair;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;


class AssignmentPairEditor extends FormLayout {
    private final TextField partOneField = new TextField("Teil A");
    private final TextField partTwoField = new TextField("Teil B");
    private final Button deleteButton = new Button(new Icon(VaadinIcon.CLOSE));

    public AssignmentPairEditor() {
        setupUI();
    }

    public void setOnDelete(Runnable onDelete) {
        deleteButton.addClickListener(e -> onDelete.run());
    }

    private void setupUI() {
//        HorizontalLayout layout = new HorizontalLayout(partOneField, partTwoField, deleteButton);
//        layout.setDefaultVerticalComponentAlignment(Alignment.END);
//        layout.setFlexGrow(1, partOneField);
//        layout.setFlexGrow(1, partTwoField);
//
//        partOneField.setWidthFull();
//        partOneField.getStyle().set("min-width", "300px");
//
//        partTwoField.setWidthFull();
//        partTwoField.getStyle().set("min-width", "300px");
//
//        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
//        deleteButton.setTooltipText("Dieses Zuordnungspaar löschen");
//        deleteButton.getStyle().set("margin-bottom", "6px");
//
//        add(layout);
//        setPadding(false);
//        setSpacing(false);
//        getStyle().set("margin", "0");
//        getStyle().set("padding", "0");
//
//        HorizontalLayout layout = new HorizontalLayout(partOneField, partTwoField, deleteButton);
//        layout.setDefaultVerticalComponentAlignment(Alignment.END);
//        layout.setPadding(false);
//        layout.setSpacing(false);
//        layout.getStyle().set("margin", "0");
//        layout.getStyle().set("padding", "0");
//        layout.setWidthFull();
//
//        partOneField.setWidth("45%");
//        partTwoField.setWidth("45%");
//
//        partOneField.getStyle().set("flex-shrink", "1");
//        partTwoField.getStyle().set("flex-shrink", "1");
//
//        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
//        deleteButton.setTooltipText("Dieses Zuordnungspaar löschen");
//
//        deleteButton.getStyle().set("margin", "0");
//        deleteButton.getStyle().set("padding", "0");
//
//        layout.setFlexGrow(1, partOneField, partTwoField); // optional
//
//        add(layout);
//        setResponsiveSteps(
//                new ResponsiveStep("0", 1),
//                new ResponsiveStep("600px", 2)
//        );
//
//        partOneField.setWidthFull();
//        partTwoField.setWidthFull();
//
//        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
//        deleteButton.setTooltipText("Dieses Zuordnungspaar löschen");
//
//        addFormItem(partOneField, "Teil A");
//        addFormItem(partTwoField, "Teil B");
//        add(deleteButton);  // Ohne Label, unten/rechts positioniert
//
//        setWidthFull();
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

    public AssignmentPair getAssignmentPair() {
        return new AssignmentPair(partOneField.getValue(), partTwoField.getValue());
    }
}