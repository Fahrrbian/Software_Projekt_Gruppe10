//ChristianMarkow
package com.gruppe10.usermanagement.ui.view;

import com.gruppe10.base.ui.Layout.MainLayout;
import com.gruppe10.base.ui.Layout.TimedMainLayout;
import com.gruppe10.taskmanagement.domain.TaskRepository;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@Route(value = "instructor", layout = TimedMainLayout.class)
@PageTitle("Instructor Info")
@RolesAllowed("INSTRUCTOR")
public class InstructorInfoView extends Main {
    public InstructorInfoView() {
        System.out.println("Student View geladen");
        add(new H1("Student Info"));
}
}