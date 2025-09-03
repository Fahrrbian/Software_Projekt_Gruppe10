/**
 * Author: Christian Markow
 * Date:27.05.2025
 */

package com.gruppe10.exercisemanagement.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
}

