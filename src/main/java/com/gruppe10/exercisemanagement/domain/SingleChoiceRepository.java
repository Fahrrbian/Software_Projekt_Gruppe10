package com.gruppe10.exercisemanagement.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SingleChoiceRepository extends JpaRepository<SingleChoice, Long>, JpaSpecificationExecutor<SingleChoice> {
}
