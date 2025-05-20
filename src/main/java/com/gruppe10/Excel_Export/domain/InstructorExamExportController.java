package com.gruppe10.Excel_Export.domain;

import com.gruppe10.Excel_Export.service.ExcelExportUtil;
import com.gruppe10.base.ui.security.SecurityUtils;
import com.gruppe10.exam.domain.Exam;
import com.gruppe10.exam.service.ExamService;
import com.gruppe10.submission.domain.Submission;
import com.gruppe10.submission.service.SubmissionService;
import com.gruppe10.usermanagement.domain.User;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

        Exam exam = examService.getById(id);
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
    }


