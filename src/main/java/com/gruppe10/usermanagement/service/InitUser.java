package com.gruppe10.usermanagement.service;

import com.gruppe10.usermanagement.domain.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * InitUser.java
 * <p>
 * Created by Fabian Holtapel on 08.05.2025.
 * <p>
 * Description:
 * Nur zum Testzwekcum encodieren zu testen, bei Insert Values über PSQL klappt das nicht so gut
 */
@Component
@DependsOn("passwordEncoder")
public class InitUser {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    public InitUser(UserRepository userRepository, BCryptPasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    @PostConstruct
    public void init() {
        userRepository.findByEmail("test@example.com").ifPresent(user -> {
            user.setPassword(encoder.encode("Password"));
            userRepository.save(user);
            System.out.println("✔ Passwort wurde einmalig verschlüsselt.");
        });
        userRepository.findByEmail("student@example.com").ifPresent(user -> {
            user.setPassword(encoder.encode("Password"));
            userRepository.save(user);
            System.out.println("✔ Passwort wurde einmalig verschlüsselt.");
        });
    }
}
