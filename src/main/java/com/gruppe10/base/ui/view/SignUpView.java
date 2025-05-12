package com.gruppe10.base.ui.view;

import com.gruppe10.usermanagement.domain.Role;
import com.gruppe10.usermanagement.service.UserService;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.IntegerField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * SignUpView.java
 * <p>
 * Created by Fabian Holtapel on 12.05.2025.
 * <p>
 * Description:
 * TODO: Beschreibung einfügen.
 */
@Route("signup")
@PageTitle("Sign Up")
@AnonymousAllowed
public class SignUpView extends VerticalLayout {
    private final UserService userService;


    private final EmailField    emailField       = new EmailField("Email");
    private final TextField     forenameField    = new TextField("Vorname");
    private final TextField     surnameField     = new TextField("Nachname");
    private final PasswordField passwordField    = new PasswordField("Passwort");
    private final ComboBox<Role> roleCombo       = new ComboBox<>("Rolle");

    private final Button signUpButton            = new Button("Sign Up");
    private final Button loginRedirectButton     = new Button("Already have an account? Log In");

    @Autowired
    public SignUpView(UserService userService) {
        this.userService = userService;

        roleCombo.setItems(Role.values());
        roleCombo.setItemLabelGenerator(r ->
                r.name().substring(0,1)
                        + r.name().substring(1).toLowerCase()
        );

        signUpButton.addClickListener(e -> signUp());
        loginRedirectButton.addClickListener(e ->
                getUI().ifPresent(ui -> ui.navigate("login"))
        );

        add(new H1("Sign Up"),
                new HorizontalLayout(emailField, forenameField, surnameField),
                passwordField,
                roleCombo,
                signUpButton,
                loginRedirectButton
        );
    }

    private void signUp() {
        String email    = emailField.getValue();
        String forename = forenameField.getValue();
        String surname  = surnameField.getValue();
        String pass     = passwordField.getValue();
        Role   role     = roleCombo.getValue();

        if (email.isEmpty() || forename.isEmpty() || surname.isEmpty()
                || pass.isEmpty()  || role == null) {
            Notification.show("Bitte alle Felder ausfüllen", 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        try {
            userService.registerUser(email, pass, forename, surname, role);
            Notification.show("Registrierung erfolgreich! Bitte einloggen.", 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            getUI().ifPresent(ui -> ui.navigate("login"));
        } catch (IllegalArgumentException e) {
            Notification.show(e.getMessage(), 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        } catch (Exception e) {
            Notification.show("Fehler: " + e.getMessage(), 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}