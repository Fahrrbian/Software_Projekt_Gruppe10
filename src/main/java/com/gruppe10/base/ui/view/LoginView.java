//Fabian Holtapel
package com.gruppe10.base.ui.view;

import com.gruppe10.base.ui.security.SecurityUtils;
import com.gruppe10.usermanagement.domain.User;
import com.gruppe10.usermanagement.service.UserService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.apache.catalina.security.SecurityUtil;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.login.LoginForm;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Route("login")
@PageTitle("Login | Online Testat")
@AnonymousAllowed
@CssImport("./styles/login-view.css")
public class LoginView extends VerticalLayout {
	private Button signup;
	private LoginForm loginForm ;
	private final H1 title;
	@Autowired
	private UserService userService;

	@Autowired @Lazy
	private PasswordEncoder passwordEncoder;

	 /*
	public LoginView() {

		addClassName("login-view");
		setSizeFull();
		setAlignItems(Alignment.CENTER);
		setJustifyContentMode(JustifyContentMode.CENTER);
		this.signup = new Button("Sign Up");
		this.signup.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		this.login = new LoginForm();

		addButtons();

		add(
				new H1("Online Testat Login"),signup, login);
	}
	private void addButtons() {
		/**
		 * Hide the forgot password button on the login form.

		login.setForgotPasswordButtonVisible(false);

		/**
		 * Set the action for the login form to "login".

		login.setAction("auth/login");
		login.getElement().executeJs(
				"const form = this;" +
						"const tf = form.shadowRoot.querySelector('vaadin-text-field');" +
						"if (tf) tf.setAttribute('name', 'username');"
		);

		/**
		 * Add a click listener to the sign up button to navigate to the sign up page.

		signup.addClickListener(e->signup());
	}

	/**
	 * Method to handle the sign up button click event.

	private void signup() {
		/**
		 * Navigate to the sign up page.

		getUI().ifPresent(ui -> ui.navigate("/signup"));
	}*/
	public LoginView() {
		addClassName("login-view");
		setSizeFull();
		setAlignItems(Alignment.CENTER);
		setJustifyContentMode(JustifyContentMode.CENTER);

		title = new H1("Online-Testat");
		title.addClassName("login-title");

		loginForm = new LoginForm();
		loginForm.addClassName("login-form");
		loginForm.setForgotPasswordButtonVisible(false);



		loginForm.addLoginListener(evt -> {
			String username = evt.getUsername();
			String rawPassword = evt.getPassword();

			var userOpt = userService.findByEmail(username);

			if (userOpt.isEmpty()
					|| !passwordEncoder.matches(rawPassword, userOpt.get().getPassword())) {
				loginForm.setError(true);
			} else {
				var user = userOpt.get();
				var token = new UsernamePasswordAuthenticationToken(
						user, null, user.getAuthorities());
				SecurityContextHolder.getContext().setAuthentication(token);



				UI.getCurrent().navigate("/");
			}
		});

		signup = new Button("Sign Up");
		signup.addClassName("signup-button");
		signup.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
		signup.addClickListener(e -> UI.getCurrent().navigate("signup"));
		add(title, loginForm, signup);
	}
/*
	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		Location loc = event.getLocation();
		if (loc.getQueryParameters().getParameters().containsKey("error")) {
			loginForm.setError(true);
		}
		if (loc.getQueryParameters().getParameters().containsKey("login")) {
			UI.getCurrent().navigate("main");
		}
	}*/
}
