/**
 * Author: Christian Markow
 * Date: 29/04/2025
 */

package com.gruppe10.usermanagement.ui.view;

import com.gruppe10.base.ui.component.ViewToolbar;
import com.gruppe10.exam.domain.Exam;
import com.gruppe10.exam.domain.ExamRepository;
import com.gruppe10.usermanagement.domain.Role;
import com.gruppe10.usermanagement.domain.User;
import com.gruppe10.usermanagement.service.UserService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Route("user-info")
@PageTitle("User Info")
@RolesAllowed({"INSTRUCTOR", "STUDENT"})
public class UserInfoView extends VerticalLayout {

    private final UserService userService;

    @Autowired
    private ExamRepository examRepository;

    UserInfoView(UserService userService, ExamRepository examRepository) {
        this.userService = userService;
        this.examRepository = examRepository;
        setPadding(true);
        setSpacing(true);
        setWidthFull();
        add(new ViewToolbar("Benutzerprofil"));

        //Hier evtl. anpassen an AuthenticatedUser
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserDetails userDetails) {
            Optional<User> optionalUser = userService.findByEmail(userDetails.getUsername());
            optionalUser.ifPresent(user -> { initUserInfoView(user); });
        } else {
            add(new Div("Fehler beim Laden des Benutzers"));
        }
    }

    private void initUserInfoView(User user) {
        add(new H2("Profil von " + user.getForename() + " " + user.getSurname()));

        // Gemeinsame Informationen
        FormLayout formLayout = new FormLayout();
        formLayout.addFormItem(new Span(user.getForename() + " " + user.getSurname()), "Name");
        formLayout.addFormItem(new Span("********"), "Passwort");
        formLayout.addFormItem(new Span(user.getEmail()), "E-Mail");
        formLayout.addFormItem(new Button("Passwort ändern", e -> {
            showChangePasswordDialog(user);
        }), "");
        add(formLayout);

        // Rollenspezifische Inhalte
        if ("INSTRUCTOR".equals(user.getRoleAsString())) {
            add(new H3("Verwaltung"));
            add(new Button("Aufgaben", e -> {
                UI.getCurrent().navigate("exercises");
            }));
            add(new Button("Prüfungen", e -> {
                UI.getCurrent().navigate("pruefung-list");
            }));
        } else if ("STUDENT".equals(user.getRoleAsString())) {
            add(new H3("Prüfungshistorie"));

            Grid<Exam> examGrid = new Grid<>(Exam.class, false);
            examGrid.addColumn(exam -> exam.getTitle()).setHeader("Modul");
            examGrid.addColumn(exam -> exam.getCreationDate()).setHeader("Prüfungstermin");
            examGrid.addColumn(exam -> exam.getGesamtpunkte()).setHeader("Note"); //Hier benötigen wir eine Punktzahl, aus der sich die Note errechnen lässt. Berechnung in einer separaten Methode.

            List<Exam> examHistory = examRepository.findByCreator(user); //Hier benötigen wir die Prüfungen eines Studenten (vllt. mit Prüfungstermin-Entität)
            examGrid.setItems(examHistory);

            examGrid.setWidth("90%");
            examGrid.setAllRowsVisible(true);
            add(examGrid);
        }
    }

    private void showChangePasswordDialog(User user) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Passwort ändern");
        dialog.setWidth("70%");
        dialog.setMaxWidth("500px");

        PasswordField oldPasswordField = new PasswordField("Altes Passwort");
        oldPasswordField.setWidthFull();
        oldPasswordField.setRequired(true);
        oldPasswordField.setAutofocus(true);
        PasswordField newPasswordField = new PasswordField("Neues Passwort");
        newPasswordField.setWidthFull();
        newPasswordField.setRequired(true);
        newPasswordField.setMinLength(5);
        PasswordField confirmPasswordField = new PasswordField("Neues Passwort wiederholen");
        confirmPasswordField.setWidthFull();
        confirmPasswordField.setRequired(true);

        Span errorMessage = new Span();
        errorMessage.getStyle().set("color", "red");

        VerticalLayout layout = new VerticalLayout(oldPasswordField, newPasswordField, confirmPasswordField, errorMessage);
        layout.setPadding(false);
        layout.setSpacing(true);
        dialog.add(layout);

        Button saveButton = new Button("Speichern", e -> {
            errorMessage.setText("");
            try {
                userService.changePassword(user, oldPasswordField.getValue(), newPasswordField.getValue(), confirmPasswordField.getValue());
                Notification.show("Passwort erfolgreich geändert", 3000, Notification.Position.BOTTOM_CENTER);
                dialog.close();
            } catch (IllegalArgumentException ex) {
                errorMessage.setText(ex.getMessage());
            }
        });

        Button cancelButton = new Button("Abbrechen", e -> dialog.close());
        dialog.getFooter().add(cancelButton, saveButton);
        dialog.open();
    }

}