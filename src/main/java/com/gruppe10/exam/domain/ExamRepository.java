package com.gruppe10.exam.domain;

/**
 * Author: Henrik Struckmeier
 * Date: 30/04/2025
 **/

import com.gruppe10.usermanagement.domain.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ExamRepository extends JpaRepository<Exam, Long>, JpaSpecificationExecutor<Exam>{

    // If you don't need a total row count, Slice is better than Page.
    //↑ Übernommen aus Vaadin.start
    Slice<Exam> findAllBy(Pageable pageable);
    List<Exam> findByCreator(User creator);

}





