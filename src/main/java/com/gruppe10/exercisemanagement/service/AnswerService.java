/**
 * Author: Christian Markow
 * Date: 27.05.2025
 */

package com.gruppe10.exercisemanagement.service;

import com.gruppe10.exercisemanagement.domain.Answer;
import com.gruppe10.exercisemanagement.domain.AnswerRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class AnswerService {

    private final AnswerRepository answerRepository;

    public AnswerService(AnswerRepository answerRepository) {
        this.answerRepository = answerRepository;
    }

    public void saveAnswers(Collection<Answer> answers) {
        answerRepository.saveAll(answers);
    }
}

