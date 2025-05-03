//ChristianMarkow
package com.gruppe10.usermanagement.service;

import com.gruppe10.usermanagement.domain.Instructor;
import com.gruppe10.usermanagement.domain.InstructorRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class InstructorService {

    private final InstructorRepository instructorRepository;

    InstructorService(InstructorRepository instructorRepository) {
        this.instructorRepository = instructorRepository;
    }

    public void createInstructor(String email, String forename, String surname, String password) {
        if ("test".equals(email)) {
            throw new RuntimeException("This is for testing the error handler");
        }
        var instructor = new Instructor();
        instructor.setEmail(email);
        instructor.setForename(forename);
        instructor.setSurname(surname);
        instructor.setPassword(password);
        instructorRepository.saveAndFlush(instructor);
    }

    public List<Instructor> list(Pageable pageable) {
        return instructorRepository.findAllBy(pageable).toList();
    }

}