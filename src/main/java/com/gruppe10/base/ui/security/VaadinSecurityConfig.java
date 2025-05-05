
package com.gruppe10.base.ui.security;
/**
 * TestView.java
 * <p>
 * Created by Fabian Holtapel on 04.05.2025.
 * <p>
 * Description:
 * TODO: Beschreibung einfügen.
 */

import com.gruppe10.base.ui.view.LoginView;
import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;


@Configuration
@EnableWebSecurity
public class VaadinSecurityConfig extends VaadinWebSecurity  {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            super.configure(http);
            setLoginView(http, LoginView.class);

            http.headers(headers -> headers
                    .frameOptions(frame -> frame.sameOrigin())  // <- wichtig!


            );
        }

        @Bean //Testing
        public UserDetailsService userDetailsService() {
            // Mock-In-Memory-User mit Rolle USER -> NO DATABASE
            return new InMemoryUserDetailsManager(
                    User.withUsername("user")
                            .password("{noop}userpass") // {noop} = kein Passwort-Hashing (nur für Testzwecke!)
                            .roles("USER")
                            .build()
            );
        }
    }

