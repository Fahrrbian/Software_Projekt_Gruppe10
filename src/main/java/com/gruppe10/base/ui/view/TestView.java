package com.gruppe10.base.ui.view;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

/**
 * TestView.java
 * <p>
 * Created by Fabian Holtapel on 04.05.2025.
 * <p>
 * Description:
 * TODO: Beschreibung einfügen.
 */

@Route("test")
public class TestView extends VerticalLayout {
    public TestView()  {
        add(new H1("Test"));
    }




}
