package com.gruppe10.base.ui.view;

import com.gruppe10.usermanagement.domain.Role;
import com.gruppe10.usermanagement.service.UserService;
import com.vaadin.flow.component.dependency.CssImport;
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
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.data.validator.EmailValidator;


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
@CssImport("./styles/signup-view.css")
public class SignUpView extends VerticalLayout {
    private final UserService userService;

    private final EmailField    emailField    = new EmailField("E-Mail");
    private final PasswordField passwordField = new PasswordField("Passwort");
    private final PasswordField confirmField  = new PasswordField("Passwort bestätigen");
    private final Checkbox     termsCheckbox = new Checkbox("AGB akzeptieren");
    private final ProgressBar  pwStrength     = new ProgressBar(0, 1, 0);
    private final Button       signUpButton   = new Button("Registrieren");
    private final Anchor       loginLink      = new Anchor("login", "Bereits angemeldet? Einloggen");

    private final Binder<RegistrationModel> binder = new Binder<>(RegistrationModel.class);

    @Autowired
    public SignUpView(UserService userService) {
        this.userService = userService;

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);

        // Card
        Div card = new Div();
        card.addClassName("signup-card");
        card.setWidth("400px");

        // Titel
        H2 title = new H2("Neues Konto anlegen");
        title.addClassName("signup-title");

        // FormLayout
        FormLayout form = new FormLayout();
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("600px", 1)
        );
        form.addFormItem(emailField,    "E-Mail");
        form.addFormItem(passwordField, "Passwort");
        form.addFormItem(confirmField,  "Passwort bestätigen");
        form.add(termsCheckbox);
        form.addFormItem(pwStrength,    "Passwort-Stärke");
        pwStrength.setWidthFull();
        pwStrength.setVisible(false);

        // Passwort-Stärke berechnen
        passwordField.addValueChangeListener(e -> {
            double s = calculateStrength(e.getValue());
            pwStrength.setValue(s);
            pwStrength.setVisible(true);
            String color = s < 0.3 ? "var(--lumo-error-color)"
                    : s < 0.7 ? "var(--lumo-primary-text-color)"
                    : "var(--lumo-success-color)";
            pwStrength.getStyle().set("background-color", color);
        });

        // Binder-Regeln
        binder.forField(emailField)
                .asRequired("E-Mail nötig")
                .withValidator(new EmailValidator("Ungültige E-Mail"))
                .bind(RegistrationModel::getEmail, RegistrationModel::setEmail);

        binder.forField(passwordField)
                .asRequired("Passwort nötig")
                .bind(RegistrationModel::getPassword, RegistrationModel::setPassword);

        binder.forField(confirmField)
                .asRequired("Bestätigung nötig")
                .withValidator(pw -> pw.equals(passwordField.getValue()),
                        "Passwörter stimmen nicht überein")
                .bind(RegistrationModel::getConfirm, RegistrationModel::setConfirm);

        binder.forField(termsCheckbox)
                .asRequired("AGB muss akzeptiert werden")
                .bind(RegistrationModel::isTerms, RegistrationModel::setTerms);

        binder.addStatusChangeListener(e ->
                signUpButton.setEnabled(binder.isValid())
        );

        signUpButton.setEnabled(false);
        signUpButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        signUpButton.addClickListener(e -> submitForm());

        // Footer
        Div footer = new Div(loginLink);
        loginLink.getStyle().set("font-size", "0.9em");

        // Zusammensetzen
        VerticalLayout layout = new VerticalLayout(title, form, signUpButton, footer);
        layout.setPadding(false);
        layout.setSpacing(true);
        layout.setAlignItems(Alignment.CENTER);

        card.add(layout);
        add(card);
    }

    private void submitForm() {
        RegistrationModel data = new RegistrationModel();
        try {
            binder.writeBean(data);
            // echten Service-Call
            userService.registerUser(
                    data.getEmail(),
                    data.getPassword(),
                    null,           // kein Vorname-Feld im Binding
                    null,           // kein Nachname
                    Role.STUDENT    // z.B. fest vorgeben
            );
            Notification.show("Erfolgreich registriert!").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            UI.getCurrent().navigate("login");
        } catch (ValidationException ex) {
            // Binder zeigt Fehlermeldungen unter den Feldern automatisch an
        } catch (Exception ex) {
            Notification.show("Fehler: " + ex.getMessage(), 3000,
                            Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    public static class RegistrationModel {
        private String email, password, confirm;
        private boolean terms;
        // Getter/Setter …
        public String getEmail() { return email; }
        public void setEmail(String e) { email = e; }
        public String getPassword() { return password; }
        public void setPassword(String p) { password = p; }
        public String getConfirm() { return confirm; }
        public void setConfirm(String c) { confirm = c; }
        public boolean isTerms() { return terms; }
        public void setTerms(boolean t) { terms = t; }
    }

    private double calculateStrength(String pw) {
        if (pw == null) return 0;
        double score = Math.min(1, pw.length() / 12.0);
        if (pw.matches(".*\\d.*")) score += 0.1;
        if (pw.matches(".*[A-Z].*")) score += 0.1;
        if (pw.matches(".*[^a-zA-Z0-9].*")) score += 0.1;
        return Math.min(1, score);
    }
}

    /* Basic Layout
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
*/