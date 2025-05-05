package com.gruppe10.base.ui.security;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * SecurityUtils.java
 * <p>
 * Created by Fabian Holtapel on 04.05.2025.
 * <p>
 * Description:
 * Supporting class to check if user is logged in
 */


public class SecurityUtils {
    public static boolean isUserLoggedIn() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }

}
