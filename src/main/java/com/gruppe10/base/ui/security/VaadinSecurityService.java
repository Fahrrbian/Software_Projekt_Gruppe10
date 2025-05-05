package com.gruppe10.base.ui.security;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinServletRequest;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;

/**
 * TestView.java
 * <p>
 * Created by Fabian Holtapel on 04.05.2025.
 * <p>
 * Description:
 * TODO: Beschreibung einfügen.
 */

@Component
public class VaadinSecurityService {
    public void logout() {
        UI.getCurrent().getPage().setLocation("/");
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(VaadinServletRequest.getCurrent().getHttpServletRequest(), null, null);
    }

}
