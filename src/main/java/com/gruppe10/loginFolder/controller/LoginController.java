package com.gruppe10.loginFolder.controller;

import com.gruppe10.usermanagement.domain.User;
import com.gruppe10.usermanagement.domain.UserRepository;
import com.gruppe10.usermanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * LoginController.java
 * <p>
 * Created by Fabian Holtapel on 17.05.2025.
 * <p>
 * Description:
 * Postmappi9ng nach dem Login um Principal zu vergeben, damit spring weiss, wer eingeloggt ist
 */
@RestController
@RequestMapping("/auth")
public class LoginController {
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String email, @RequestParam String password) {
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null || !user.getPassword().equals(password)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login fehlgeschlagen");
        }

        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(token);

        return ResponseEntity.ok("Erfolgreich eingeloggt");
    }
}
