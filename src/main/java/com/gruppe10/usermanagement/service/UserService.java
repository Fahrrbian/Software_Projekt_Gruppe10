/**
 * Author: Christian Markow
 * Date: 29/04/2025
 */

package com.gruppe10.usermanagement.service;

import com.gruppe10.usermanagement.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Service
public class UserService implements UserDetailsService {

    /**
     * Autowired UserRepository instance.
     */
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private InstructorRepository instructorRepository;
    @Autowired @Lazy
    private PasswordEncoder passwordEncoder;

    /**
     * Loads a user by username, used by Spring Security for authentication.
     *
     * @param email the username to search for.
     * @return a UserDetails object representing the user.
     * @throws UsernameNotFoundException if the user is not found.
     */

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            var userObj = user.get();
            System.out.println(userObj);
            return org.springframework.security.core.userdetails.User.builder()
                    .username(userObj.getEmail())
                    .password(userObj.getPassword())
                    .roles(userObj.getRole())
                    .build();
        } else {
            throw new UsernameNotFoundException(email);
        }
    }

    /**
     * Saves a User entity to the database.
     *
     * @param user the User entity to be saved.
     */
    public void save(User user) {
        userRepository.save(user);
    }

    /*
    public User registerUser(String email,
                             String rawPassword,
                             String forename,
                             String surname,
                             Role role) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("E-Mail bereits vergeben");
        }

        User user;
        if (role == Role.STUDENT) {
            user = new Student();
        } else {
            user = new Instructor();
        }

        user.setEmail(email);
        user.setForename(forename);
        user.setSurname(surname);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(role);
        int next = userRepository.findMaxStudentNumber() + 1;
        user.setStudentNumber(next);

        return userRepository.save(user);
    }
     */
    @Transactional
    public Student registerStudent(String email, String rawPassword,
                                   String forename, String surname) {
        // E-Mail-Check, encode, Role zu STUDENT …
        Student s = new Student();
        s.setEmail(email);
        s.setForename(forename);
        s.setSurname(surname);
        s.setPassword(passwordEncoder.encode(rawPassword));
        s.setRole(Role.STUDENT);
        int next = userRepository.findMaxStudentNumber() + 1;
        s.setStudentNumber(next);
        return studentRepository.save(s);
    }

    public Instructor registerInstructor(String email, String rawPassword,
                                         String forename, String surname) {
        Instructor i = new Instructor();
        i.setEmail(email);
        i.setForename(forename);
        i.setSurname(surname);
        i.setPassword(passwordEncoder.encode(rawPassword));
        i.setRole(Role.INSTRUCTOR);
        return instructorRepository.save(i);
    }

    /**
     * Retrieves all User entities from the database.
     *
     * @return a list of all User entities.
     */
    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void changePassword(User user, String oldPassword, String newPassword, String confirmPassword) {
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Altes Passwort ist falsch.");
        }

        if (newPassword == null || newPassword.trim().isEmpty() || newPassword.length() < 5) {
            throw new IllegalArgumentException("Das neue Passwort muss mindestens 5 Zeichen lang sein.");
        }

        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("Neues Passwort stimmt nicht überein.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

}

    /**
     * Logs out the current user and redirects to the login page.

    public void logout() {
        UI.getCurrent().getPage().setLocation("/login");
        var logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(VaadinServletRequest.getCurrent(), null, null);
    }*/
