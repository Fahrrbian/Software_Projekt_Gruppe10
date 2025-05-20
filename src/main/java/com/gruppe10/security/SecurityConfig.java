package com.gruppe10.security;

import com.gruppe10.base.ui.view.LoginView;
import com.gruppe10.usermanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.vaadin.flow.spring.security.VaadinWebSecurity;


@EnableWebSecurity
@Configuration
public class SecurityConfig extends VaadinWebSecurity {

    /**
     * Autowired instance of the UserServiceImpl, which provides user details for authentication.
     */
    @Autowired
    private UserService userDetailService;

    /**
     * Configures the HTTP security settings for the application.
     *
     * @param http the HttpSecurity object to configure
     * @throws Exception if an error occurs during configuration
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Call the superclass method to configure the security settings
        super.configure(http);
        // Set the login view to the LoginView class
        setLoginView(http, LoginView.class);
    }



    /**
     * Creates a bean for the UserDetailsService, which provides user details for authentication.
     *
     * @return the UserDetailsService bean
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return userDetailService;
    }

    /**
     * Creates a bean for the PasswordEncoder, which is used to encode passwords.
     *
     * @return the PasswordEncoder bean
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Creates a bean for the AuthenticationProvider, which is used to authenticate users.
     *
     * @return the AuthenticationProvider bean
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();

        // Set the userDetailsService to the autowired instance
        provider.setUserDetailsService(userDetailService);

        // Set the passwordEncoder to the created bean
        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

}