//ChristianMarkow
package com.gruppe10.usermanagement.service;

import com.gruppe10.usermanagement.domain.Student;
import com.gruppe10.usermanagement.domain.StudentRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class StudentService {

    private final StudentRepository studentRepository;

    StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public void createStudent(String email, String forename, String surname, String password, int studentNumber) {
        if ("test".equals(email)) {
            throw new RuntimeException("This is for testing the error handler");
        }
        var student = new Student();
        student.setEmail(email);
        student.setForename(forename);
        student.setSurname(surname);
        student.setPassword(password);
        student.setStudentNumber(studentNumber);
        studentRepository.saveAndFlush(student);
    }

    public List<Student> list(Pageable pageable) {
        return studentRepository.findAllBy(pageable).toList();
    }

}
