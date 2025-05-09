/**
 * Author: Christian Markow
 * Date: 29/04/2025
 */

package com.gruppe10.usermanagement.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@DiscriminatorValue("STUDENT")
public class Student extends User {

    @Column(name = "student_number", nullable = false, length = MAX_LENGTH, unique = true)
    @Min(1)
    @Max(999999999)
    private int studentNumber;

    public int getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(int studentNumber) {
        this.studentNumber = studentNumber;
    }

}