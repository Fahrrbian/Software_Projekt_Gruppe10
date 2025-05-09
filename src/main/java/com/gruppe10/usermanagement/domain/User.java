/**
 * Author: Christian Markow
 * Date: 29/04/2025
 */

package com.gruppe10.usermanagement.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gruppe10.base.domain.AbstractEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.jspecify.annotations.Nullable;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING)
@Table(name = "users")
public abstract class User extends AbstractEntity<Long> {

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
    @Size(max = MAX_LENGTH)
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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

}