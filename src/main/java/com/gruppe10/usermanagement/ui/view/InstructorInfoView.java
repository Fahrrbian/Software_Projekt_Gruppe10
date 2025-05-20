/**
 * Author: Christian Markow
 * Date: 29/04/2025
 */

package com.gruppe10.usermanagement.ui.view;

import com.gruppe10.base.ui.view.MainLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route(value = "instructor", layout = MainLayout.class)
@PageTitle("Instructor Info")
@RolesAllowed("INSTRUCTOR")
public class InstructorInfoView extends Main {
    public InstructorInfoView() {
        System.out.println("Student View geladen");
        add(new H1("Student Info"));
}
}