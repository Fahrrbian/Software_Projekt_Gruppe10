package com.gruppe10.Excel_Export.domain;

import com.gruppe10.Excel_Export.service.ExcelExportUtil;
import com.gruppe10.base.ui.security.SecurityUtils;
import com.gruppe10.examManagement.exam.domain.Exam;
import com.gruppe10.examManagement.exam.service.ExamService;
import com.gruppe10.submission.DTOs.ReviewDto;
import com.gruppe10.submission.domain.Submission;
import com.gruppe10.submission.DTOs.SubmissionDto;
import com.gruppe10.submission.domain.SubmissionStatus;
import com.gruppe10.submission.service.SubmissionService;
import com.gruppe10.usermanagement.domain.User;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * InstructorExamExportController.java
 * <p>
 * Created by Fabian Holtapel on 18.05.2025.
 * <p>
 * Description:
 * Der Instructor braucht auch einen eigenen Controller, da hier nach ID exportiert wird bei Student gilt nur public ResponseEntity<byte[]> exportExamResultsToExcel()
 */
@RestController
@RequestMapping("/api/instructor/exams")
@RolesAllowed("INSTRUCTOR")
public class InstructorExamExportController {

    private final ExamService examService;
    private final SubmissionService submissionService;

    @Autowired
    public InstructorExamExportController(ExamService examService, SubmissionService submissionService) {
        this.examService = examService;
        this.submissionService = submissionService;
    }
    @GetMapping("/export/{id}")
    public ResponseEntity<byte[]> exportExamResultsForInstructor(@PathVariable Long id) throws IOException {
        Optional<User> currentUser = SecurityUtils.getCurrentUser();
        if(currentUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Exam exam = examService.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if(exam == null ||  !exam.getCreator().getId().equals(currentUser.get().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<Submission> submissions = submissionService.getSubmissionsByExam(exam);
        if(submissions == null || submissions.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        byte[] excel = ExcelExportUtil.generateExcelInsturctor(submissions);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=exam-results-" + id + ".xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excel);
        }

    @PostMapping("/{examId}/release")
    public ResponseEntity<Void> releaseAll(@PathVariable Long examId) {
        //Hiermit werden alle Exams released
        Exam exam = examService.getById(examId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        exam.setAutoPublishResults(true);
        examService.save(exam);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/submissions/{subId}/review")
    public ResponseEntity<SubmissionDto> reviewSubmission(
            @PathVariable Long subId,
            @RequestBody ReviewDto dto) {

        Submission sub = submissionService.findById(subId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // Punkte aus dto (Freitext) übernehmen
        dto.getUpdatedPoints().forEach((qid, pts) ->
                sub.getAufgabenErgebnisse().put(qid, pts)
        );
        sub.setTotalPoints(sub.calculateTotal());
        sub.setStatus(SubmissionStatus.REVIEWED);

        Submission saved = submissionService.save(sub);
        return ResponseEntity.ok(SubmissionDto.from(saved));
    }
    }


