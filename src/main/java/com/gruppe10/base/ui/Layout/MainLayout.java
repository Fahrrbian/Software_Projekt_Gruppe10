package com.gruppe10.base.ui.Layout;

import com.gruppe10.security.AuthenticatedUser;
import com.gruppe10.usermanagement.service.UserService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.avatar.AvatarVariant;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.server.menu.MenuEntry;
import com.vaadin.flow.theme.lumo.LumoUtility;

import static com.vaadin.flow.theme.lumo.LumoUtility.*;

/**
 * InitUser.java
 * <p>
 * Created by Fabian Holtapel on 08.05.2025.
 * Fixed by Christian Markow on 26.05.2025.
 * <p>
 * Description:
 * Standard-Layout
 */

public class MainLayout extends AppLayout {

    private final AuthenticatedUser authenticatedUser;
    protected HorizontalLayout header;

    public MainLayout(AuthenticatedUser authenticatedUser) {
        this.authenticatedUser = authenticatedUser;
        setPrimarySection(AppLayout.Section.DRAWER);
        createHeader();
        createDrawer();
    }

    protected void createHeader() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.getStyle().set("cursor", "pointer");

        H2 headerText = new H2("Online-Testat");
        headerText.addClassNames(
                LumoUtility.FontSize.LARGE,
                LumoUtility.Margin.LARGE);

        Button logout = new Button("Logout", e -> {
            authenticatedUser.logout();
            UI.getCurrent().getPage().setLocation("/login");
        });
        logout.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        logout.getStyle().set("cursor", "pointer");
        logout.addClassName(LumoUtility.Margin.Left.AUTO);

        header = new HorizontalLayout(toggle, headerText, logout);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setWidthFull();
        header.expand(headerText);
        header.addClassNames(
                LumoUtility.Padding.Vertical.NONE,
                LumoUtility.Padding.Horizontal.MEDIUM);

        addToNavbar(header);
    }

    protected void createDrawer() {
        String username = getFullName();

        HorizontalLayout userInfo = new HorizontalLayout(new H3("Menü"));
        userInfo.setAlignItems(FlexComponent.Alignment.CENTER);
        userInfo.getStyle().set("padding-left", "0.5rem");
        userInfo.getStyle().set("padding-top", "0.3rem");
        userInfo.getStyle().set("padding-bottom", "1rem");

        addToDrawer(userInfo, new Scroller(createSideNav()), createUserInfo());
    }

    private SideNav createSideNav() {
        var nav = new SideNav();
        nav.addClassNames(Margin.Horizontal.MEDIUM, Margin.Vertical.MEDIUM);

        authenticatedUser.get().ifPresent(user -> {
            String role = user.getRole();

            nav.addItem(new SideNavItem("Prüfungstermine", "exam-appointments", VaadinIcon.CALENDAR.create()));

            if ("INSTRUCTOR".equalsIgnoreCase(role)) {
                nav.addItem(new SideNavItem("Aufgabenübersicht", "exercises", VaadinIcon.RECORDS.create()));
                nav.addItem(new SideNavItem("Aufgabenerstellung", "create-exercise", VaadinIcon.FORM.create()));
                nav.addItem(new SideNavItem("Prüfungsübersicht", "pruefung-list", VaadinIcon.RECORDS.create()));
                //nav.addItem(new SideNavItem("Prüfungserstellung", "pruefung-form", VaadinIcon.FORM.create()));
                nav.addItem(new SideNavItem("Prüfungskorrektur", "exams-to-correct", VaadinIcon.CLIPBOARD_CHECK.create()));
                nav.addItem(new SideNavItem("Prüfungsergebnisse", "auswertung", VaadinIcon.LIST_OL.create()));
            } else if ("STUDENT".equalsIgnoreCase(role)) {
                nav.addItem(new SideNavItem("Anstehende Prüfungen", "student-pruefung-list", VaadinIcon.CLIPBOARD.create()));
                nav.addItem(new SideNavItem("Prüfungsergebnisse", "pruefungsergebnisse", VaadinIcon.LIST_OL.create()));
            }

            nav.addItem(new SideNavItem("Profil", "user-info", VaadinIcon.COGS.create()));
        });

        //MenuConfiguration.getMenuEntries().forEach(entry -> nav.addItem(createSideNavItem(entry)));
        return nav;
    }

    private Component createUserInfo() {
        String fullName = getFullName();

        var avatar = new Avatar(fullName);
        avatar.addThemeVariants(AvatarVariant.LUMO_XSMALL);
        avatar.setColorIndex(5);

        var userName = new Span(fullName);

        var avatarAndUserName = new HorizontalLayout(avatar, userName);
        avatarAndUserName.setAlignItems(FlexComponent.Alignment.CENTER);
        avatarAndUserName.setPadding(false);
        avatarAndUserName.setMargin(false);
        avatarAndUserName.getStyle().set("padding-left", "0.5rem");

        var userMenu = new MenuBar();
        userMenu.addThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE);
        userMenu.addClassNames(Margin.MEDIUM);

        var userMenuItem = userMenu.addItem(avatarAndUserName);

        return userMenu;
    }

    private SideNavItem createSideNavItem(MenuEntry menuEntry) {
        if (menuEntry.icon() != null) {
            return new SideNavItem(menuEntry.title(), menuEntry.path(), new Icon(menuEntry.icon()));
        } else {
            return new SideNavItem(menuEntry.title(), menuEntry.path());
        }
    }

    private String getFullName() {
        return authenticatedUser.get().map(user -> user.getForename() + " " + user.getSurname())
                .orElseThrow(() -> new IllegalStateException("Kein Benutzer eingeloggt"));
    }

}