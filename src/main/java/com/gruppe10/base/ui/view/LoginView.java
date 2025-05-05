//Fabian Holtapel
package com.gruppe10.base.ui.view;

import com.gruppe10.base.ui.security.SecurityUtils;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterListener;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.apache.catalina.security.SecurityUtil;

@Route("login")
@PageTitle("Login | Online Testat")
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterListener {
	
	private LoginForm login = new LoginForm(); 
	
	public LoginView() {
		login.setAction("login");
		addClassName("loginView");
		setSizeFull();
		setAlignItems(Alignment.CENTER);
		setJustifyContentMode(JustifyContentMode.CENTER);



		add(
				new H1("Online Testat Login"), login);
	}
	@Override
	public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
		if(SecurityUtils.isUserLoggedIn()) {
			beforeEnterEvent.forwardTo(MainLayout.class);
		}

		if(beforeEnterEvent.getLocation()
				.getQueryParameters()
				.getParameters()
				.containsKey("error")) {
			login.setError(true);
		}
	}
}
