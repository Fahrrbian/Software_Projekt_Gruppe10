//ChristianMarkow
package com.gruppe10.usermanagement.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@DiscriminatorValue("STUDENT")
public class Student extends User implements UserDetails {

    public Student() {
        super();
    }

    @Column(name = "student_number", nullable = false, unique = true)
    //@Size(max = MAX_LENGTH)
    @NotNull
    @Min(1)
    @Max(999999999)
    private int studentNumber;

    public int getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(int studentNumber) {
        this.studentNumber = studentNumber;
    }

    @Override
    public String getUsername() {
        return getEmail();
    }
}