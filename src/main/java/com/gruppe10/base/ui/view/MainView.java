package com.gruppe10.base.ui.view;

import com.gruppe10.base.ui.component.ViewToolbar;
import com.gruppe10.usermanagement.domain.User;
import com.gruppe10.security.AuthenticatedUser;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;

import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This view shows up when a user navigates to the root ('/') of the application.
 */
@Route("/")
@PermitAll
@PageTitle("Start | Online Testat")
public class MainView extends VerticalLayout implements BeforeEnterObserver {

    private final AuthenticatedUser authenticatedUser;



    @Autowired
    public MainView(AuthenticatedUser authenticatedUser) {
        this.authenticatedUser = authenticatedUser;

        add(new H1("Du wirst gleich weitergeleitet ..."));
    }
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        authenticatedUser.get().ifPresent(user -> {
            String role = user.getRole();
           if ("INSTRUCTOR".equalsIgnoreCase(role)) {
                event.forwardTo("auswertung");
            } else {
                event.forwardTo("pruefungsergebnisse");
            }
        });
    }
    }
/**
 * Navigates to the main view.

public static void showMainView() {
    UI.getCurrent().navigate(MainView.class);
}
*/
