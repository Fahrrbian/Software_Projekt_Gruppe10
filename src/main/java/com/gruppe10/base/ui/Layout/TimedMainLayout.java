package com.gruppe10.base.ui.Layout;

import com.gruppe10.security.AuthenticatedUser;
import com.gruppe10.usermanagement.service.UserService;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.stereotype.Component;

/**
 * TimedMainLayout.java
 * <p>
 * Created by Fabian Holtapel on 14.05.2025.
 * <p>
 * Description:
 * TODO: Beschreibung einfügen.
 */
@Component
@UIScope
public class TimedMainLayout extends MainLayout implements RouterLayout {

    public TimedMainLayout(UserService userService, AuthenticatedUser authenticatedUser) {
        super(userService, authenticatedUser);
        HorizontalLayout header = new HorizontalLayout();
        // (… dein bestehender MainLayout-Header …)
        Div idleIndicator = new Div();
        idleIndicator.setId("idle-indicator");
        header.add(idleIndicator);
        addToNavbar(header);

        //JavaScript-Code der Timer setzt und Aktivität ermittelt
        getElement().executeJs(
                "(function(){"
                        + "  const server = $0.$server;"
                        + "  let timer = setTimeout(()=>server.onIdle(), 30*60*1000);"
                        + "  ['mousemove','keydown','click','scroll','touchstart']"
                        + "    .forEach(evt=>document.addEventListener(evt,()=>{"
                        + "      clearTimeout(timer);"
                        + "      let mins = Math.ceil((30*60*1000 - (Date.now() % (30*60*1000))) / (60*1000));"
                        + "      document.getElementById('idle-indicator').textContent = mins;"
                        + "      timer = setTimeout(()=>server.onIdle(),30*60*1000);"
                        + "    }));"
                        + "})();"
                , getElement());
    }


    @ClientCallable
    public void onIdle() {
        VaadinSession.getCurrent().getSession().invalidate();
        getUI().ifPresent(ui -> ui.getPage().setLocation("/login"));
    }
}
