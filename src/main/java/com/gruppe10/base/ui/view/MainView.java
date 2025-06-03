package com.gruppe10.base.ui.view;

import com.gruppe10.base.ui.Layout.MainLayout;
import com.gruppe10.usermanagement.domain.User;
import com.gruppe10.security.AuthenticatedUser;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This view shows up when a user navigates to the root ('/') of the application.
 */
@Route(value = "/", layout = MainLayout.class)
@PageTitle("Startseite")
@RolesAllowed({"INSTRUCTOR", "STUDENT"})
public class MainView extends VerticalLayout implements BeforeEnterObserver {

    private final AuthenticatedUser authenticatedUser;

    @Autowired
    public MainView(AuthenticatedUser authenticatedUser) {
        this.authenticatedUser = authenticatedUser;
        setSizeFull();
        setAlignItems(FlexComponent.Alignment.CENTER);
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        authenticatedUser.get().ifPresent(user -> {
            initMainView(user);
        });
    }

    public void initMainView(User user) {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidth("50%");
        layout.setPadding(true);
        layout.setSpacing(true);
        layout.setAlignItems(FlexComponent.Alignment.STRETCH);

        if ("INSTRUCTOR".equalsIgnoreCase(user.getRole())) {
            layout.add(createCard("Aufgabenübersicht", "Verwaltung von Aufgaben", "exercises"));
            layout.add(createCard("Prüfungsübersicht", "Verwaltung von Prüfungen", "pruefung-list"));
            layout.add(createCard("Prüfungskorrektur", "Korrektur von Prüfungen", "exam-correction"));
            layout.add(createCard("Prüfungsergebnisse", "Auswertungen von Prüfungen", "auswertung"));
            layout.add(createCard("Profil", "Verwaltung von persönlichen Daten", "user-info"));
        } else if ("STUDENT".equalsIgnoreCase(user.getRole())) {
            layout.add(createCard("Anstehende Prüfungen", "Übersicht über anstehende Prüfungen", "PLATZHALTER"));
            layout.add(createCard("Prüfungsergebnisse", "Überblick über Prüfungsergebnisse", "pruefungsergebnisse"));
            layout.add(createCard("Profil", "Verwaltung von persönlichen Daten", "user-info"));
        }

        add(layout);
    }

    private Component createCard(String title, String description, String navigationTarget) {
        Div card = new Div();
        card.setWidthFull();
        card.getStyle()
                .set("border", "1px solid #ccc")
                .set("border-radius", "12px")
                .set("padding", "20px")
                .set("cursor", "pointer")
                .set("background-color", "#f9f9f9")
                .set("transition", "background-color 0.3s")
                .set("box-shadow", "0 2px 4px rgba(0,0,0,0.1)");

        card.add(new H3(title), new Paragraph(description));

        card.addClickListener(e -> {
            UI.getCurrent().navigate(navigationTarget);
        });

        // Hover-Effekt
        card.getElement().executeJs(
                "this.addEventListener('mouseover', () => this.style.backgroundColor = '#e6f7ff');" +
                        "this.addEventListener('mouseout', () => this.style.backgroundColor = '#f9f9f9');"
        );

        return card;
    }

}