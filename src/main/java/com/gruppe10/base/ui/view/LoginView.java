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
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.login.LoginForm;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("login")
@PageTitle("Login | Online Testat")
@AnonymousAllowed
public class LoginView extends VerticalLayout {
	private Button signup;
	private LoginForm login ;
	
	public LoginView() {

		addClassName("loginView");
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
		 */
		login.setForgotPasswordButtonVisible(false);

		/**
		 * Set the action for the login form to "login".
		 */
		login.setAction("login");

		/**
		 * Add a click listener to the sign up button to navigate to the sign up page.
		 */
		signup.addClickListener(e->signup());
	}

	/**
	 * Method to handle the sign up button click event.
	 */
	private void signup() {
		/**
		 * Navigate to the sign up page.
		 */
		getUI().ifPresent(ui -> ui.navigate("/signup"));
	}

}
