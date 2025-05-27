package com.gruppe10.Excel_Export.domain;

import com.gruppe10.Excel_Export.service.ExcelExportUtil;
import com.gruppe10.base.ui.security.SecurityUtils;
import com.gruppe10.submission.domain.Submission;
import com.gruppe10.submission.service.SubmissionService;
import com.gruppe10.usermanagement.domain.Student;
import com.gruppe10.usermanagement.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.*;

/**
 * ExamExportController.java
 * <p>
 * Created by Fabian Holtapel on 13.05.2025.
 * <p>
 * Description:
 * TODO: Beschreibung einfügen.
 */
@RestController
@RequestMapping("/api/student")
public class ExamExportController {

    private final SubmissionService submissionService;

    @Autowired
    public ExamExportController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportMyExamResults() throws IOException {
        Optional<User> currentUser = SecurityUtils.getCurrentUser();

        if (currentUser.isEmpty() || !(currentUser.get() instanceof Student student)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        //List<Submission> submissions = submissionService.getSubmissionsByStudent(student);
        List<Submission> submissions = submissionService.getSubmissionsByStudentFullyFetched(student);

        if (submissions.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        byte[] excel = ExcelExportUtil.generateExcelStudent(submissions);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=meine-pruefungsergebnisse.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excel);
    }
}
/*
    @Autowired
    private ExamService examService;

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportExamResultsToExcel(@PathVariable Long id) throws IOException {
        Exam exam = examService.getById(id);
        if (exam == null) {
            return ResponseEntity.notFound().build();
        }

        List<Submission> results = examService.getExamResultsByExam(exam);

        if (results == null || results.isEmpty()) {
            System.out.println("No exam results found");
            return ResponseEntity.noContent().build();
        }
        byte[] excel = ExcelExportUtil.generateExcelStudent(results);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=exam-results.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excel);

    }
}
*/