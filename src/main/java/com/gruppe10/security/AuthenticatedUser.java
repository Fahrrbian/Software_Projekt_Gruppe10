package com.gruppe10.security;

import com.gruppe10.usermanagement.domain.User;
import com.gruppe10.usermanagement.domain.UserRepository;
import com.vaadin.flow.spring.security.AuthenticationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.Optional;

/**
 * AuthenticatedUser.java
 * <p>
 * Created by Fabian Holtapel on 08.05.2025.
 * <p>
 * Description:
 * TODO: Beschreibung einfügen.
 */

@Component
@SessionScope
public class AuthenticatedUser {

    private final UserRepository userRepository;

    @Autowired
    public AuthenticatedUser(AuthenticationContext authenticationContext, UserRepository userRepository) {
        this.authenticationContext = authenticationContext;
        this.userRepository = userRepository;
    }

    private final AuthenticationContext authenticationContext;

    public Optional<User> get() {
        return authenticationContext.getAuthenticatedUser(UserDetails.class)
                .flatMap(userDetails -> userRepository.findByEmail(userDetails.getUsername()));
    }

    public void logout() {
        authenticationContext.logout();
    }
}
