package com.gruppe10.base.ui.Layout;

import com.gruppe10.base.ui.view.MainView;
import com.gruppe10.security.AuthenticatedUser;
import com.gruppe10.usermanagement.domain.User;
import com.gruppe10.usermanagement.service.UserService;
import com.gruppe10.usermanagement.ui.view.InstructorInfoView;
import com.gruppe10.usermanagement.ui.view.StudentInfoView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.menu.MenuConfiguration;
import com.vaadin.flow.server.menu.MenuEntry;
import com.vaadin.flow.theme.lumo.LumoUtility;


import static com.vaadin.flow.theme.lumo.LumoUtility.*;

/**
 * InitUser.java
 * <p>
 * Created by Fabian Holtapel on 08.05.2025.
 * <p>
 * Description:
 * Standard-Layout
 */

@SuppressWarnings("unused")
@CssImport("./styles/MainLayout.css")  // lädt Dein globales MainLayout.css
public class MainLayout extends AppLayout {

    private final UserService userService;
    private final AuthenticatedUser authenticatedUser;
    protected HorizontalLayout header;

    // wird in Unterklassen (z.B. TimedMainLayout) via super(...) aufgerufen
    public MainLayout(UserService userService,
                      AuthenticatedUser authenticatedUser) {
        this.userService       = userService;
        this.authenticatedUser = authenticatedUser;

        // Sidebar (Drawer) links, Navbar oben
        setPrimarySection(Section.DRAWER);
        createDrawer();
        createHeader();
    }

    private void createHeader() {
        // 1) Logo / Titel
        H1 logo = new H1("Online-Testat");

        // 2) DrawerToggle + Logo + (optionaler Platzhalter rechts)
        header = new HorizontalLayout(new DrawerToggle(), logo);
        header.addClassName("app-header");  // CSS-Klasse aus MainLayout.css
        header.setWidthFull();
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        addToNavbar(header);
    }

    private void createDrawer() {
        // --- 1) User-Info ganz oben ---
        String email = authenticatedUser.get()
                .map(u -> u.getEmail())
                .orElse("anonymous");
        Image logoImg = new Image("/icons/AppIcon.png", "Logo");
        logoImg.addClassName("drawer-logo-img");
        HorizontalLayout logoLayout = new HorizontalLayout(logoImg, new Span(email));
        logoLayout.addClassName("drawer-logo");
        logoLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        RouterLink home = new RouterLink(MainView.class);
        home.add(new Icon(VaadinIcon.HOME), new Span("Startseite"));
        home.setClassName("drawer-link-item");

        RouterLink exams = new RouterLink();
        exams.add(new Icon(VaadinIcon.FILE_TEXT), new Span("Prüfungen"));
        exams.setClassName("drawer-link-item");
        // --- 2) Navigations-Links ---
        VerticalLayout links = new VerticalLayout();
        links.addClassName("drawer-links");
        links.setPadding(false);
        links.setSpacing(false);
        links.add(home, exams);

        String role = authenticatedUser.get()
                .map(u -> u.getRole())
                .orElse("");
        /*
        if ("INSTRUCTOR".equals(role)) {
            links.add(new RouterLink("Instructor", InstructorInfoView.class));
        } else if ("STUDENT".equals(role)) {
            links.add(new RouterLink("Student", StudentInfoView.class));
        }*/
        // … weitere Links nach Bedarf …

        // --- 3) Logout-Button immer unten ---
        Button logout = new Button("Log out", e -> {
            authenticatedUser.logout();
            UI.getCurrent().getPage().setLocation("/login");
        });
        logout.addClassName("drawer-logout");

        // --- 4) Alles in eine Flex-Spalte packen ---
        VerticalLayout drawerContent = new VerticalLayout(
                logoLayout,
                links,
                logout
        );
        drawerContent.addClassName("drawer-content");
        drawerContent.setSizeFull();
        drawerContent.setPadding(false);
        drawerContent.setSpacing(false);
        // sorgt dafür, dass 'links' den Zwischenraum einnimmt und logout nach unten schiebt
        drawerContent.setFlexGrow(1, links);

        addToDrawer(drawerContent);
    }
}