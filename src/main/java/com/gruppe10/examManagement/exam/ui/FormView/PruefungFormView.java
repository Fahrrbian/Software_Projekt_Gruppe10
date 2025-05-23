package com.gruppe10.examManagement.exam.ui.FormView;

/**
 * Author: Henrik Struckmeier
 * Date: 02/05/2025
 **/


import com.gruppe10.base.ui.component.ViewToolbar;
import com.gruppe10.examManagement.exam.domain.Exam;
import com.gruppe10.examManagement.exam.service.ExamService;
import com.gruppe10.taskmanagement.service.TaskService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.ServletConfig;

@Route(value = "pruefung-form/:id")
@RouteAlias(value = "pruefung-form")
@PageTitle("Prüfung")
@Menu(order = 0, icon = "vaadin:clipboard-check", title = "Prüfungseditor")
@PermitAll
public class PruefungFormView extends VerticalLayout implements HasUrlParameter<Long> {

    private final ExamService examService;
    private final ServletConfig servletConfig;
    private Exam IExamInterface;
    private TextField title;
    private PruefungForm form;
    private TaskGrid taskGrid;
    private Button createBtn;
    private Button backBtn;
    private Button saveBtn;


    public PruefungFormView(ExamService examService, TaskService taskService, ServletConfig servletConfig) {
        this.examService = examService;
        this.taskGrid = new TaskGrid(taskService);
        this.servletConfig = servletConfig;


        setSizeFull();
        addClassNames(LumoUtility.BoxSizing.BORDER,
                LumoUtility.Display.FLEX,
                LumoUtility.FlexDirection.COLUMN,
                LumoUtility.Padding.MEDIUM,
                LumoUtility.Gap.SMALL);

    }

    private void initializeComponents() {
        title = new TextField("Titel");
        title.setValue(IExamInterface != null ? IExamInterface.getTitle() : "");

        createBtn = new Button("Create", event -> createPruefung());
        createBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        // Zurück-Button erstellen
        backBtn = new Button("Zurück", event -> navigateBack());
        backBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        backBtn.setIcon(new Icon(VaadinIcon.ARROW_LEFT));

        // Speichern-Button erstellen
        saveBtn = new Button("Speichern", event -> savePruefung());
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveBtn.setIcon(new Icon(VaadinIcon.CHECK));

        Button terminButton = new Button("Zu den Prüfungsterminen");
        terminButton.addClickListener(e -> {
            terminButton.getUI().ifPresent(ui ->
                    ui.navigate("exam-appointments/" + IExamInterface.getId())
            );
        });

        form = new PruefungForm(examService, IExamInterface);

        removeAll(); // Entfernt alle vorherigen Komponenten
        add(new ViewToolbar("Prüfung bearbeiten",
                ViewToolbar.group(backBtn, title, createBtn, saveBtn, terminButton)));
        add(form);
        add(taskGrid);

        if (IExamInterface != null && IExamInterface.getId() != null) {
            taskGrid.setPruefungId(IExamInterface.getId());
        }

    }

    @Override
    public void setParameter(BeforeEvent event, Long parameter) {
        try {
            this.IExamInterface = parameter != null ?
                    examService.getById(parameter) :
                    new Exam();
        } catch (Exception e) {
            this.IExamInterface = new Exam();
        }
        initializeComponents();
    }


    private void createPruefung() {
        Exam IExamInterface = new Exam();
    }

    private void navigateBack() {
        UI.getCurrent().navigate("pruefung-list");
    }

    private void savePruefung() {
        try {
            if (IExamInterface != null) {
                IExamInterface.setTitle(form.getTitle());
                IExamInterface.setBestehensgrenze(form.getBestehensgrenze());
                IExamInterface.setGesamtpunkte(form.getGesamtpunkte());

                examService.updatePruefung(IExamInterface, IExamInterface.getId());

                Notification.show("Prüfung erfolgreich gespeichert",
                                3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            }
        } catch (Exception e) {
            Notification.show("Fehler beim Speichern: " + e.getMessage(),
                            3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}




