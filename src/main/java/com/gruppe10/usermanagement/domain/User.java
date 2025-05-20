//ChristianMarkow
package com.gruppe10.usermanagement.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gruppe10.base.domain.AbstractEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING)
@Table(name = "users")
public abstract class User extends AbstractEntity<Long> implements UserDetails {

    public static final int MAX_LENGTH = 255;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @NotBlank
    @Column(name = "email", nullable = false, length = MAX_LENGTH, unique = true)
    @Size(max = MAX_LENGTH)
    private String email;

    @NotBlank
    @Column(name = "forename", nullable = false, length = MAX_LENGTH)
    @Size(max = MAX_LENGTH)
    private String forename;

    @NotBlank
    @Column(name = "surname", nullable = false, length = MAX_LENGTH)
    @Size(max = MAX_LENGTH)
    private String surname;

    @NotBlank
    @Column(name = "password", nullable = false, length = MAX_LENGTH)
    @Size(max = MAX_LENGTH)
    private String password;


    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = MAX_LENGTH)
    //@Size(max = MAX_LENGTH)
    @NotNull
    private Role role;



    @Override
    public @Nullable Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getForename() {
        return forename;
    }

    public void setForename(String forename) {
        this.forename = forename;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return String.valueOf(role);
    }

    public String getRoleAsString() {
        return role.name(); // oder role.toString()
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + getRole()));
    }

}