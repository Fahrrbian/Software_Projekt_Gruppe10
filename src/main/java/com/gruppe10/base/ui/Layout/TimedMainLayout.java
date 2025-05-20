package com.gruppe10.base.ui.Layout;

import com.gruppe10.security.AuthenticatedUser;
import com.gruppe10.usermanagement.service.UserService;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
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
@CssImport("./styles/MainLayout.css")
public class TimedMainLayout extends MainLayout implements RouterLayout {

    private final Div idleIndicator = new Div("30min");;

    public TimedMainLayout(UserService userService, AuthenticatedUser authenticatedUser) {
        super(userService, authenticatedUser);
        //HorizontalLayout header = new HorizontalLayout();
        idleIndicator.setId("idle-indicator");
        //header.addComponentAtIndex(1, idleIndicator);

        header.add(idleIndicator);
    }

        // addToNavbar(header);
/*
    @Override
            protected void createHeader() {
        super.createHeader();
        idleIndicator.setId("idle-indicator");
        header.add(idleIndicator);
        //addToNavbar(idleIndicator);
    }*/
        @Override
        protected void onAttach(AttachEvent attachEvent) {
            super.onAttach(attachEvent);
            installIdleJs();  // selbe JS-Logik wie vorher
        }
    private void installIdleJs() {
        getElement().executeJs(
                "const idleEl=$0;\n"
                        + "const server=$1.$server;\n"
                        + "const T=30*60*1000;\n"
                        + "let last=Date.now();\n"
                        + "let timer;\n"
                        + "\n"
                        + "function updateIndicator() {\n"
                        + "  const elapsed = Date.now() - last;\n"
                        + "  const minsLeft = Math.ceil((T - elapsed) / 60000);\n"
                        + "  idleEl.textContent = minsLeft + ' Min';\n"
                        + "  console.log('Timer tick: elapsed=' + Math.floor(elapsed/1000) + 's, left=' + minsLeft + 'm');\n"
                        + "}\n"
                        + "\n"
                        + "function reset() {\n"
                        + "  last = Date.now();\n"
                        + "  clearTimeout(timer);\n"
                        + "  timer = setTimeout(()=>{ console.log('onIdle fired'); server.onIdle(); }, T);\n"
                        + "  console.log('resetTimer at ' + new Date().toISOString());\n"
                        + "  updateIndicator();\n"
                        + "}\n"
                        + "\n"
                        + "reset();\n"
                        + "setInterval(updateIndicator, 60*1000); // alle 60 s nochmal updaten\n"
                        + "['mousemove','keydown','click','scroll','touchstart']\n"
                        + "  .forEach(evt=>document.addEventListener(evt, reset));",
                idleIndicator.getElement(), getElement()
        );
    }
/*
    private void installIdleJs() {
            // Jz ist das DOM da — wir injizieren das Idle-Timer-Skript
            getElement().executeJs(
                    "const idleEl = $0;"                              // $0 → dein idleIndicator-Element
                            + "const server = $1.$server;"                      // $1 → das Host-Element für den @ClientCallable-Proxy"
                            + "const timeout = 30 * 60 * 1000;"                 // 30 Minuten
                            + "let lastActivity = Date.now();"
                            + "let timer = setTimeout(()=>server.onIdle(), timeout);"
                            + ""
                            + "function resetTimer() {"
                            + "  lastActivity = Date.now();"
                            + "  clearTimeout(timer);"
                            + "  timer = setTimeout(()=>server.onIdle(), timeout);"
                            + "  updateIndicator();"
                            + "}"
                            + ""
                            + "function updateIndicator() {"
                            + "  const minsLeft = Math.ceil((timeout - (Date.now() - lastActivity)) / 60000);"
                            + "  idleEl.textContent = minsLeft + ' Min';"
                            + "}"
                            + ""
                            + "updateIndicator();"
                            + "['mousemove','keydown','click','scroll','touchstart']"
                            + "  .forEach(evt => document.addEventListener(evt, resetTimer));"
                    , idleIndicator.getElement(), getElement()
            );
        }
*/

/*
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
*/

    @ClientCallable
    public void onIdle() {
        VaadinSession.getCurrent().getSession().invalidate();
        UI.getCurrent().getPage().setLocation("/login");
        //getUI().ifPresent(ui -> ui.getPage().setLocation("/login"));
    }
}
