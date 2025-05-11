//ChristianMarkow
package com.gruppe10.base.ui.view;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;


@Route(value="student", layout = MainLayout.class)
@PageTitle("Student Info")
@RolesAllowed("STUDENT")
public class StudentInfoView extends VerticalLayout {
    public StudentInfoView() {
        System.out.println("Student View geladen");
        add(new H1("Student Info"));
    }
}
