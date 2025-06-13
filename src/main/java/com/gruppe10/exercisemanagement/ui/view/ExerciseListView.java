package com.gruppe10.exercisemanagement.ui.view;

import com.gruppe10.base.ui.Layout.MainLayout;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.gruppe10.exercisemanagement.domain.Exercise;
import com.gruppe10.exercisemanagement.domain.Tag;
import com.gruppe10.exercisemanagement.service.ExerciseService;
import com.gruppe10.exercisemanagement.service.TagService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;

import java.util.*;
import java.util.stream.Collectors;

@Route(value="exercises", layout = MainLayout.class)
@PageTitle("Aufgaben verwalten")
@Menu(order = 2, icon = "vaadin:records", title = "Aufgabenübersicht")
@RolesAllowed("INSTRUCTOR")
public class ExerciseListView extends VerticalLayout  {

    private final ExerciseService exerciseService;
    private final TagService tagService;

    private final Grid<Exercise> exerciseGrid;
    private final VerticalLayout filterLayout;

    private final MultiSelectComboBox<Tag> tagFilterComboBox = new MultiSelectComboBox<>("Tags filtern");
    private final ComboBox<String> typeFilterComboBox = new ComboBox<>("Typ filtern");
    private final Button manageTagsButton = new Button("Tags verwalten", event -> openTagManagementDialog());

    private Dialog tagManagementDialog;
    private TextField tagSearchField;
    private VerticalLayout tagsContainer;

    public ExerciseListView(ExerciseService exerciseService, TagService tagService) {
        this.exerciseService = exerciseService;
        this.tagService = tagService;

        addClassNames(LumoUtility.Padding.MEDIUM, LumoUtility.BoxSizing.BORDER);
        setSizeFull();

        filterLayout = createFilterBar();

        exerciseGrid = new Grid<>(Exercise.class, false);
        configureExerciseGrid();

        Button createButton = new Button("+Neue Aufgabe", event -> createExercise());

        add(filterLayout, createButton, exerciseGrid);
        updateGrid(null, null);
    }

    private VerticalLayout createFilterBar() {
        VerticalLayout filterBar = new VerticalLayout();
        filterBar.setPadding(false);
        filterBar.setSpacing(true);

        tagFilterComboBox.setItemLabelGenerator(Tag::getName);
        tagFilterComboBox.setItems(tagService.getAll());
        tagFilterComboBox.setClearButtonVisible(true);
        tagFilterComboBox.setPlaceholder("Tags auswählen...");
        tagFilterComboBox.setWidth("25%");

        typeFilterComboBox.setItems("Freitextaufgabe", "Single Choice", "Multiple Choice", "Zuordnungsaufgabe");
        typeFilterComboBox.setPlaceholder("Typ auswählen...");
        typeFilterComboBox.setClearButtonVisible(true);
        typeFilterComboBox.setWidth("25%");

        tagFilterComboBox.addValueChangeListener(event -> updateGrid(tagFilterComboBox.getValue(), typeFilterComboBox.getValue()));
        typeFilterComboBox.addValueChangeListener(event -> updateGrid(tagFilterComboBox.getValue(), typeFilterComboBox.getValue()));

        manageTagsButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        HorizontalLayout filterRow = new HorizontalLayout(tagFilterComboBox, typeFilterComboBox, manageTagsButton);
        filterRow.setWidthFull();
        filterRow.setAlignItems(FlexComponent.Alignment.BASELINE);
        filterRow.add(tagFilterComboBox, typeFilterComboBox);

        com.vaadin.flow.component.html.Div spacer = new com.vaadin.flow.component.html.Div();
        spacer.getStyle().set("flex-grow", "1");
        filterRow.add(spacer);

        filterRow.add(manageTagsButton);
        filterBar.add(filterRow);
        return filterBar;
    }

    private void configureExerciseGrid() {
        exerciseGrid.addColumn(Exercise::getExerciseText).setHeader("Aufgabentext").setWidth("40%").setFlexGrow(0);
        exerciseGrid.addColumn(Exercise::getScore).setHeader("Punkte");
        exerciseGrid.addColumn(e ->
                e.getTags().stream().map(Tag::getName).collect(Collectors.joining(", "))
        ).setHeader("Tags");
        exerciseGrid.addColumn(this::resolveType).setHeader("Typ");
        exerciseGrid.addComponentColumn(this::createEditButton).setHeader("Aktion");

        exerciseGrid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);
        exerciseGrid.setSizeFull();
    }

    // Übersetzt die Namen in die ichtige Anzeige
    private String resolveType(Exercise exercise) {
        if (exercise.getClass().getSimpleName().equals("FreetextExercise")) {
            return "Freitextaufgabe";
        } else if (exercise.getClass().getSimpleName().equals("SingleChoice")) {
            return "Single Choice";
        } else if (exercise.getClass().getSimpleName().equals("MultipleChoice")) {
            return "Multiple Choice";
        } else if (exercise.getClass().getSimpleName().equals("AssignmentExercise")) {
            return "Zuordnungsaufgabe";
        }
        return "Unbekannt";
    }

    // Navigiert zur Detailansicht einer Übung zur Bearbeitung
    private Button createEditButton(Exercise exercise) {
        return new Button("Bearbeiten", click ->
                getUI().ifPresent(ui -> ui.navigate(ExerciseDetailView.class, new RouteParameters("exerciseId", String.valueOf(exercise.getId()))))
        );
    }

    private void updateGrid(Set<Tag> selectedTags, String selectedType) {
        Pageable pageable = PageRequest.of(0, 100);

        var allExercises = exerciseService.getAll(pageable).getContent();


        var filtered = allExercises.stream()
                .filter(e -> selectedTags == null || selectedTags.isEmpty() ||
                        e.getTags().stream().anyMatch(selectedTags::contains))
                .filter(e -> selectedType == null || selectedType.equals(resolveType(e)))
                .toList();

        exerciseGrid.setItems(filtered);
    }

    private void createExercise() {
        getUI().ifPresent(ui -> ui.navigate("create-exercise"));
    }

    private void openTagManagementDialog() {
        tagManagementDialog = new Dialog();
        tagManagementDialog.setWidth("50%");
        tagManagementDialog.setHeight("auto");
        tagManagementDialog.setCloseOnEsc(true);
        tagManagementDialog.setCloseOnOutsideClick(true);

        VerticalLayout dialogContentLayout = new VerticalLayout();
        dialogContentLayout.setPadding(true);
        dialogContentLayout.setSpacing(true);
        dialogContentLayout.setSizeFull();

        HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.setWidthFull();
        headerLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        headerLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        headerLayout.add(new H3("Tags verwalten"));

        Button closeButtonInHeader = new Button(new Icon(VaadinIcon.CLOSE), e -> tagManagementDialog.close());
        closeButtonInHeader.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
        closeButtonInHeader.setTooltipText("Dialog schließen");
        headerLayout.add(closeButtonInHeader);
        dialogContentLayout.add(headerLayout);

        tagSearchField = new TextField();
        tagSearchField.setPlaceholder("Tags suchen...");
        tagSearchField.setWidthFull();
        tagSearchField.setValueChangeMode(ValueChangeMode.EAGER);
        tagSearchField.addValueChangeListener(event -> updateTagListInDialog(event.getValue()));
        dialogContentLayout.add(tagSearchField);

        Button openAddTagDialogButton = new Button("Neuen Tag hinzufügen", event -> openAddTagDialog());
        openAddTagDialogButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        dialogContentLayout.add(openAddTagDialogButton);

        tagsContainer = new VerticalLayout();
        tagsContainer.setPadding(false);
        tagsContainer.setSpacing(false);
        tagsContainer.setWidthFull();
        tagsContainer.setHeight("70%");
        tagsContainer.getStyle().set("overflow-y", "auto");
        dialogContentLayout.add(tagsContainer);
        dialogContentLayout.setFlexGrow(1, tagsContainer);

        updateTagListInDialog("");

        tagManagementDialog.add(dialogContentLayout);
        tagManagementDialog.open();
    }

    private void openAddTagDialog() {
        Dialog addTagDialog = new Dialog();
        addTagDialog.setHeaderTitle("Neuen Tag hinzufügen");
        addTagDialog.setCloseOnEsc(true);
        addTagDialog.setCloseOnOutsideClick(true);

        VerticalLayout addTagLayout = new VerticalLayout();
        addTagLayout.setPadding(true);
        addTagLayout.setSpacing(true);
        addTagLayout.setWidth("100%");

        TextField newTagNameField = new TextField("Tag-Name");
        newTagNameField.setPlaceholder("Name des neuen Tags...");
        newTagNameField.setWidthFull();
        addTagLayout.add(newTagNameField);

        Button saveNewTagButton = new Button("Hinzufügen", event -> {
            String newTagName = newTagNameField.getValue().trim();
            if (!newTagName.isEmpty()) {
                if (tagService.findByName(newTagName).isPresent()) {
                    Notification.show("Tag '" + newTagName + "' existiert bereits.", 3000, Notification.Position.MIDDLE);
                } else {
                    try {
                        Tag newTag = new Tag(newTagName);
                        tagService.create(newTag);
                        Notification.show("Tag '" + newTagName + "' erfolgreich hinzugefügt.", 3000, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                        addTagDialog.close();
                        updateTagListInDialog(tagSearchField.getValue());
                        tagFilterComboBox.setItems(tagService.getAll());
                    } catch (Exception e) {
                        Notification.show("Fehler beim Hinzufügen des Tags: " + e.getMessage(), 5000, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_ERROR);
                    }
                }
            } else {
                Notification.show("Tag-Name darf nicht leer sein.", 3000, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        saveNewTagButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelAddTagButton = new Button("Abbrechen", event -> addTagDialog.close());
        cancelAddTagButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        HorizontalLayout buttonLayout = new HorizontalLayout(saveNewTagButton, cancelAddTagButton);
        buttonLayout.setWidthFull();
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        addTagLayout.add(buttonLayout);

        addTagDialog.add(addTagLayout);
        addTagDialog.open();
    }

    // Aktualisiert die Tag-Liste im Dialog basierend auf Suchtext
    private void updateTagListInDialog(String searchText) {
        tagsContainer.removeAll();

        List<Tag> allTags = tagService.getAll();
        List<Tag> filteredTags = allTags.stream()
                .filter(tag -> tag.getName().toLowerCase().contains(searchText.toLowerCase()))
                .collect(Collectors.toList());

        if (filteredTags.isEmpty()) {
            tagsContainer.add(new com.vaadin.flow.component.html.Paragraph("Keine Tags gefunden."));
        } else {
            for (Tag tag : filteredTags) {
                HorizontalLayout tagRow = new HorizontalLayout();
                tagRow.setWidthFull();
                tagRow.setAlignItems(FlexComponent.Alignment.CENTER);

                com.vaadin.flow.component.html.Span tagNameSpan = new com.vaadin.flow.component.html.Span(tag.getName());
                tagNameSpan.getStyle()
                        .set("flex-grow", "1")
                        .set("padding", "8px 0");

                Button deleteTagButton = new Button(new Icon(VaadinIcon.CLOSE));
                deleteTagButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
                deleteTagButton.setTooltipText("Tag löschen");
                deleteTagButton.addClickListener(e -> confirmTagDeletion(tag));

                tagRow.add(tagNameSpan, deleteTagButton);
                tagsContainer.add(tagRow);
            }
        }
    }

    private void confirmTagDeletion(Tag tag) {
        ConfirmDialog dialog = new ConfirmDialog("Tag löschen", "Möchten Sie den Tag '" + tag.getName() + "' wirklich löschen? Dies kann nicht rückgängig gemacht werden.", "Löschen", event -> {
            try {
                tagService.delete(tag.getId());
                Notification.show("Tag '" + tag.getName() + "' erfolgreich gelöscht.", 3000, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_SUCCESS);

                updateTagListInDialog(tagSearchField.getValue());
                tagFilterComboBox.setItems(tagService.getAll());
                tagFilterComboBox.clear();
                updateGrid(null, null);
            } catch (DataIntegrityViolationException e) {
                Notification.show("Der Tag '" + tag.getName() + "' kann nicht gelöscht werden, da er noch Aufgaben zugewiesen ist.", 5000, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (Exception e) {
                Notification.show("Fehler beim Löschen des Tags: " + e.getMessage(), 5000, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        },
                "Abbrechen", event -> {
            Notification.show("Löschvorgang abgebrochen.", 1500, Notification.Position.MIDDLE).addThemeVariants(NotificationVariant.LUMO_ERROR);
        });
        dialog.setConfirmButtonTheme("error primary");
        dialog.open();
    }
}

