/**
 * Author: Christian Markow
 * Date: 29/04/2025
 */

package com.gruppe10.usermanagement.domain;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface InstructorRepository extends JpaRepository<Instructor, Long>, JpaSpecificationExecutor<Instructor> {

    Slice<Instructor> findAllBy(Pageable pageable);

    Optional<Instructor> findByEmail(String email);

}