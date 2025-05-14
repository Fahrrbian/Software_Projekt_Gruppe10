package com.gruppe10.usermanagement.ui.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import java.util.List;
import com.vaadin.flow.component.button.Button;

/**
 * adminView.java
 * <p>
 * Created by Fabian Holtapel on 07.05.2025.
 * <p>
 * Description:
 * TODO: Beschreibung einfügen.
 */

    @Route("admin")
    @RolesAllowed("INSTRUCTOR")
    public class adminView extends VerticalLayout {
        public adminView() {
            List<Component> components = List.of(
                    new H1("Hello Admin"),
                    new Button("Logout")
            );

            add(components.toArray(new Component[0]));
        }
    }

