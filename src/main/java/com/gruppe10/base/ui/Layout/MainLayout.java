package com.gruppe10.base.ui.Layout;

import com.gruppe10.security.AuthenticatedUser;
import com.gruppe10.usermanagement.domain.User;
import com.gruppe10.usermanagement.service.UserService;
import com.gruppe10.usermanagement.ui.view.InstructorInfoView;
import com.gruppe10.usermanagement.ui.view.StudentInfoView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;
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
public class MainLayout extends AppLayout {


    private final UserService userService;

    private final AuthenticatedUser authenticatedUser;

    protected HorizontalLayout header;

    public MainLayout(UserService userService, AuthenticatedUser authenticatedUser) {
        this.userService = userService;
        this.authenticatedUser = authenticatedUser;
        setPrimarySection(AppLayout.Section.DRAWER);
        createDrawer();
        createHeader();
        //addToDrawer(createHeader(), new Scroller(createSideNav()), createUserMenu());
    }

    protected void createDrawer() {
        String username = authenticatedUser.get().map(User::getEmail) // oder getForename() / getSurname()
                .orElseThrow(() -> new IllegalStateException("Kein Benutzer eingeloggt"));


        Button logout = new Button("Log out", e -> {
            authenticatedUser.logout();
            UI.getCurrent().getPage().setLocation("/login");
        });
        logout.addClassName("logout-Button");

        HorizontalLayout user = new HorizontalLayout(new Icon(VaadinIcon.HEADSET), new H3(username));
        user.addClassName("User-Name");

        VerticalLayout avatarAndName = new VerticalLayout(logout);
        avatarAndName.addClassNames(LumoUtility.Margin.Top.AUTO, Width.FULL);

        // Create Drawer Content
        VerticalLayout drawerContent = new VerticalLayout();
        drawerContent.setSizeFull();

        //Add links depending on role
        String role = authenticatedUser.get().map(User::getRole).orElseThrow(() -> new IllegalStateException("Keine Rolle"));
        if ("INSTRUCTOR".equals(role)) {
            drawerContent.add(new RouterLink("instructor", InstructorInfoView.class));
        } else if ("STUDENT".equals(role)) {
            drawerContent.add(new RouterLink("student", StudentInfoView.class));
        }

        drawerContent.add(avatarAndName);

        Header header = new Header(user);
        addToDrawer(header, new Scroller(drawerContent));
    }
    protected void createHeader() {
        H1 logo = new H1("Online-Testat");
        logo.addClassNames(
                LumoUtility.FontSize.LARGE,
                LumoUtility.Margin.MEDIUM);

        header = new HorizontalLayout(new DrawerToggle(), logo);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.expand(logo); // Expands the logo to fill the space
        header.setWidthFull();
        header.addClassNames(
                LumoUtility.Padding.Vertical.NONE,
                LumoUtility.Padding.Horizontal.MEDIUM);

        addToNavbar(header);
    }
        /*        // TODO Replace with real application logo and name
        var appLogo = VaadinIcon.CUBES.create();
        appLogo.addClassNames(TextColor.PRIMARY, IconSize.LARGE);

        var appName = new Span("Softwareprojekt Gruppe 10");
        appName.addClassNames(FontWeight.SEMIBOLD, FontSize.LARGE);

        var header = new Div(appLogo, appName);
        header.addClassNames(Display.FLEX, Padding.MEDIUM, Gap.MEDIUM, AlignItems.CENTER);
        return header;
    }
 Das muss lazy in einer View erzeugt werden
    private SideNav createSideNav() {
        var nav = new SideNav();
        nav.addClassNames(Margin.Horizontal.MEDIUM);
        MenuConfiguration.getMenuEntries().forEach(entry -> nav.addItem(createSideNavItem(entry)));
        return nav;
    }

    private SideNavItem createSideNavItem(MenuEntry menuEntry) {
        if (menuEntry.icon() != null) {
            return new SideNavItem(menuEntry.title(), menuEntry.path(), new Icon(menuEntry.icon()));
        } else {
            return new SideNavItem(menuEntry.title(), menuEntry.path());
        }
    }

    private Component createUserMenu() {
        // TODO Replace with real user information and actions
        var avatar = new Avatar("John Smith");
        avatar.addThemeVariants(AvatarVariant.LUMO_XSMALL);
        avatar.addClassNames(Margin.Right.SMALL);
        avatar.setColorIndex(5);

        var userMenu = new MenuBar();
        userMenu.addThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE);
        userMenu.addClassNames(Margin.MEDIUM);

        var userMenuItem = userMenu.addItem(avatar);
        userMenuItem.add("John Smith");
        userMenuItem.getSubMenu().addItem("View Profile");
        userMenuItem.getSubMenu().addItem("Manage Settings");
        userMenuItem.getSubMenu().addItem("Logout");

        return userMenu;
    }
*/
}
