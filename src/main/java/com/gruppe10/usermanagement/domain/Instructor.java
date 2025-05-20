/**
 * Author: Christian Markow
 * Date: 29/04/2025
 */

package com.gruppe10.usermanagement.domain;

import jakarta.persistence.*;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;

@Entity
@DiscriminatorValue("INSTRUCTOR")
public class Instructor extends User implements UserDetails, Serializable {


    @Override
    public String getUsername() {
        return getEmail();
    }
}