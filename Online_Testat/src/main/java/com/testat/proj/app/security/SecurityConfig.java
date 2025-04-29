package com.testat.proj.app.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import com.testat.proj.app.views.LoginView;
import com.vaadin.flow.spring.security.VaadinWebSecurity;

@EnableWebSecurity 
@Configuration
public class SecurityConfig extends VaadinWebSecurity {
	
	@Override 
	protected void configure(HttpSecurity http) throws Exception {
        // Standardkonfiguration + LoginView festlegen
        super.configure(http);
    
    //deselect Spring Image 
        http
        .authorizeHttpRequests(auth -> auth 
			.requestMatchers("/images/**").permitAll()
			.anyRequest().authenticated()
			); 
	    setLoginView(http, LoginView.class);
	}
	
    @Bean //IST NICHT GEDACHT FÜR INDUSTRIAL SOLUTIONS
    public UserDetailsService userDetailsService() {
        // In-Memory-User mit Rolle USER
        return new InMemoryUserDetailsManager(
            User.withUsername("user")
                .password("{noop}userpass") // {noop} = kein Passwort-Hashing (nur für Testzwecke!)
                .roles("USER")
                .build()
        );
    }
}
	

