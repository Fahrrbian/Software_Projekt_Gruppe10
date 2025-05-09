/**
 * Author: Christian Markow
 * Date: 29/04/2025
 */

package com.gruppe10.usermanagement.domain;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("INSTRUCTOR")
public class Instructor extends User {

}