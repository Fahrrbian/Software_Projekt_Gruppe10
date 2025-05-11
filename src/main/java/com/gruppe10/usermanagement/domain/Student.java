//ChristianMarkow
package com.gruppe10.usermanagement.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@DiscriminatorValue("STUDENT")
public class Student extends User {


    @Column(name = "student_number", nullable = false, unique = true)
    //@Size(max = MAX_LENGTH)
    @NotNull
    private int studentNumber;

    public int getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(int studentNumber) {
        this.studentNumber = studentNumber;
    }

}