//ChristianMarkow
package com.gruppe10.usermanagement.service;

import com.gruppe10.usermanagement.domain.User;
import com.gruppe10.usermanagement.domain.UserRepository;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinServletRequest;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    /**
     * Autowired UserRepository instance.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * Loads a user by username, used by Spring Security for authentication.
     *
     * @param username the username to search for.
     * @return a UserDetails object representing the user.
     * @throws UsernameNotFoundException if the user is not found.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmail(email);
        if(user.isPresent()) {
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

    /**
     * Retrieves all User entities from the database.
     *
     * @return a list of all User entities.
     */
    public List<User> findAll() {
        return userRepository.findAll();
    }

    /**
     * Logs out the current user and redirects to the login page.

    public void logout() {
        UI.getCurrent().getPage().setLocation("/login");
        var logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(VaadinServletRequest.getCurrent(), null, null);
    }*/
}