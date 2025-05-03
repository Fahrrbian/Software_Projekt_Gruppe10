//ChristianMarkow
package com.gruppe10.usermanagement.domain;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("INSTRUCTOR")
public class Instructor extends User {

}