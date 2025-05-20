package com.gruppe10.exam.ui.ListView;

/**
 * Author: Henrik Struckmeier
 * Date: 30/04/2025
 **/

import com.gruppe10.base.ui.component.ViewToolbar;
import com.gruppe10.base.ui.Layout.MainLayout;
import com.gruppe10.exam.domain.Exam;
import com.gruppe10.exam.service.ExamService;
import com.gruppe10.exam.ui.ExamListener;
import com.gruppe10.taskmanagement.domain.Task;
import com.gruppe10.usermanagement.domain.Role;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.ServletConfig;
import java.time.Clock;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Optional;
import java.util.Set;

import static com.vaadin.flow.spring.data.VaadinSpringDataHelpers.toSpringPageRequest;


@Route(value="pruefung-list", layout = MainLayout.class)
@PageTitle("Prüfung List")
//@Menu(order = 0, icon = "vaadin:clipboard-check", title = "Prüfungsliste")
@RolesAllowed("INSTRUCTOR")
//PermitAll durch richtige Rolle tauschen
public class ExamListView extends Main implements ExamListener {

    private final ExamService examService;

    final TextField title;
    final Long creator;
    final Button createBtn;
    final Button deleteBtn;
    final Grid<Exam> pruefungGrid;
    final PruefungListEditorView pruefungEditorView;
    private final ServletConfig servletConfig;
    private Long lastActiveId;

    public ExamListView(ExamService examService, Clock clock, ServletConfig servletConfig) {
        this.examService = examService;
        examService.startListening(this);

        creator = (long) 001;

        title = new TextField();
        title.setPlaceholder("Titel der neuen Prüfung");
        title.setAriaLabel("Task description");
        title.setMaxLength(Task.DESCRIPTION_MAX_LENGTH);
        title.setMinWidth("20em");

        var creatorTextField = new TextField();

        //Button zum Erstellen einer Prüfung
        createBtn = new Button("Neue Prüfung", event -> createPruefung());
        createBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        //Button zum Löschen einer Prüfung
        deleteBtn = new Button("Eintrag löschen", event -> deletePruefung());
        deleteBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        var dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).withZone(clock.getZone())
                .withLocale(getLocale());
        var dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(getLocale());

        pruefungGrid = new Grid<>();
        pruefungGrid.setItems(query -> examService.list(toSpringPageRequest(query)).stream());
        pruefungGrid.addColumn(Exam::getId).setHeader("Id");
        pruefungGrid.addColumn(Exam::getTitle).setHeader("Titel");
        pruefungGrid.addColumn(Exam::getCreatorId).setHeader("Creator ID");
//        pruefungGrid.addColumn(pruefung -> Optional.ofNullable(pruefung.()).map(dateFormatter::format).orElse("Never"))
//                .setHeader("Due Date");
        pruefungGrid.addColumn(pruefung -> dateTimeFormatter.format(pruefung.getCreationDate())).setHeader("Creation Date");
        pruefungGrid.addColumn(Exam::getGesamtpunkte).setHeader("Gesamtpunkte");
        pruefungGrid.addColumn(Exam::getBestehensgrenze).setHeader("Bestehensgrenze");

        pruefungGrid.setSizeFull();

        //Erzeugen des Editors views und Aufruf eines Refreshs wenn neue Zeile ausgewählt wird
        //Zusätzlich zum Refresh wird die aktive ID festgehalten
        pruefungEditorView = new PruefungListEditorView(this);
        pruefungGrid.addItemClickListener(event -> {
            captureActiveId();
            refreshForm();
        });
       /** pruefungGrid.addItemDoubleClickListener(event -> {
            pruefungGrid.getUI().ifPresent(ui -> ui.navigate(
                    PruefungFormView.class, lastActiveId));
        });
        **/
        pruefungGrid.addItemDoubleClickListener(event -> {
            if (event.getItem() != null) {
                try {
                    UI.getCurrent().navigate( "pruefung-form/" + event.getItem().getId()
                    );
                } catch (Exception e) {
                    Notification.show("Fehler beim Öffnen der Prüfung: " + e.getMessage(),
                            3000, Notification.Position.MIDDLE);
                }
            }
        });





        setSizeFull();
        addClassNames(LumoUtility.BoxSizing.BORDER, LumoUtility.Display.FLEX, LumoUtility.FlexDirection.COLUMN,
                LumoUtility.Padding.MEDIUM, LumoUtility.Gap.SMALL);

        add(new ViewToolbar("Meine Prüfungen", ViewToolbar.group(title, createBtn, deleteBtn)));
        add(pruefungGrid);
        add(pruefungEditorView);
        this.servletConfig = servletConfig;
    }



    private void refreshForm() {
        try {
            Optional<Exam> activeRecord = pruefungGrid.getSelectedItems().stream().findFirst();
            if (activeRecord.isPresent()) {
                Exam activeIExamInterface = activeRecord.get();
                pruefungEditorView.setTitle(activeIExamInterface.getTitle());
                pruefungEditorView.setBestehensgrenze(Double.toString(activeIExamInterface.getBestehensgrenze()));
            }

        } catch (Exception e) {
            Notification.show("Kein Refresh", 50, Notification.Position.MIDDLE);
        }
    }

    private void captureActiveId() {
        var firstItem = pruefungGrid.getSelectedItems().stream().findFirst();
        lastActiveId = firstItem.map(Exam::getId)
                .orElseGet(() -> examService.getLast().getId());
    }


    private void deletePruefung() {

        Set<Exam> pruefungenToDelete = pruefungGrid.getSelectedItems();
        for (Exam IExamInterface : pruefungenToDelete) {
            examService.removePruefung(IExamInterface.getId());
            pruefungGrid.getDataProvider().refreshAll();
            Notification.show("Prüfung gelöscht", 3000, Notification.Position.BOTTOM_END)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        }
        pruefungGrid.select(examService.getLast());

    }

    private void createPruefung() {
        examService.createPruefung(title.getValue(), creator, null);
        pruefungGrid.getDataProvider().refreshAll();
        title.clear();
        Notification.show("Task added", 3000, Notification.Position.BOTTOM_END)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    public void getUpdate() {
        refreshForm();
    }

    public void pushUpdate(Exam IExamInterface) {
        Optional<Exam> activeRecord = pruefungGrid.getSelectedItems().stream().findFirst();
        if (activeRecord.isPresent()) {
            examService.updatePruefung(IExamInterface, activeRecord.get().getId());
            pruefungGrid.getDataProvider().refreshAll();
            pruefungGrid.select(activeRecord.get());
        }
    }
}
