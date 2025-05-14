/**
 * Author: Christian Markow
 * Date: 29/04/2025
 */

package com.gruppe10.usermanagement.ui.view;

import com.gruppe10.base.ui.component.ViewToolbar;
import com.gruppe10.usermanagement.domain.Role;
import com.gruppe10.usermanagement.domain.User;
import com.gruppe10.usermanagement.service.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Optional;

@Route("user-info")
@PageTitle("User Info")
@Secured({"ROLE_INSTRUCTOR", "ROLE_STUDENT"})
public class UserInfoView extends VerticalLayout {

    private final UserService userService;

    UserInfoView(UserService userService) {
        this.userService = userService;
        setPadding(true);
        setSpacing(true);
        setWidthFull();
        add(new ViewToolbar("Benutzerprofil"));

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
                        //WeiterleitungZuAufgabenVerwaltungsView
            }));
            add(new Button("Prüfungen", e -> {
                //WeiterleitungZuPrüfungsVerwaltungsView
            }));
        } else if ("STUDENT".equals(user.getRoleAsString())) {
            add(new H3("Schüler-Details"));
            //Test
            add(new Paragraph("Aktueller Kurs: Systementwicklung"));
            //Test
            add(new Paragraph("Nächste Prüfung: " + LocalDate.now().plusWeeks(2)));
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