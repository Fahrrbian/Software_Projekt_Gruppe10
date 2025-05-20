package com.gruppe10.base.ui.security;

import com.gruppe10.usermanagement.domain.User;
import com.gruppe10.usermanagement.domain.UserRepository;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * SecurityUtils.java
 * <p>
 * Created by Fabian Holtapel on 04.05.2025.
 * <p>
 * Description:
 * Supporting class to check if user is logged in
 */

@Component
public class SecurityUtils {

    @Autowired
    private static UserRepository userRepository;

    @Autowired
    public SecurityUtils(UserRepository repo) {
        SecurityUtils.userRepository = repo;
    }

    public static boolean isUserLoggedIn() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }
    public static Optional<User> getCurrentUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof User) {
            return Optional.of((User) principal);
        }
        if (principal instanceof org.springframework.security.core.userdetails.User springUser) {
            String email = springUser.getUsername();
            return userRepository.findByEmail(email); // funktioniert als static aufruf aus repo - ohne hats nicht funktioniert
        }
        return Optional.empty();
    }

}
