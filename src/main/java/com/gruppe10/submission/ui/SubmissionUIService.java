package com.gruppe10.submission.ui;

import com.gruppe10.submission.DTOs.ReviewDto;
import com.gruppe10.submission.DTOs.SubmissionDto;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

/**
 * SubmissionUiService.java
 * <p>
 * Created by Fabian Holtapel on 03.06.2025.
 * <p>
 * Description:
 * Hier wird    API erstellt
 */
@Service
public class SubmissionUIService {

    private final WebClient client = WebClient.create("/api"); //  mit Basis‐URL ansprechen: /api

    public List<SubmissionDto> fetchSubmissionsForAppointment(String examId) {
        // hier nur eine GET-req /api/instructor/appointments/{examId}/submissions
        return client.get()
                .uri("/instructor/{examId}/submissions", examId)
                .retrieve()
                .bodyToFlux(SubmissionDto.class)
                .collectList()
                .block();
    }

    public void reviewSubmission(String subId, ReviewDto dto) {
        // hier eine PATCH-Request /api/instructor/appointments/submissions/{subId}/review
        client.patch()
                .uri("/instructor/submissions/{examId}/review", subId)
                .bodyValue(dto)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}
