/**
 * Author: Christian Markow
 * Date: 14.06.2025
 */

package com.gruppe10;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import org.springframework.stereotype.Component;

//Klasse zum Push für Live-Timer-Aktualisierung
@Push
@Theme(variant = Lumo.LIGHT)
@Component
public class AppShell implements AppShellConfigurator {
}