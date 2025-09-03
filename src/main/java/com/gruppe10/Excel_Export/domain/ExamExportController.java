package com.gruppe10.Excel_Export.domain;

import com.gruppe10.Excel_Export.service.ExcelExportUtil;
import com.gruppe10.base.ui.security.SecurityUtils;
import com.gruppe10.examManagement.exam.domain.Exam;
import com.gruppe10.examManagement.exam.service.ExamService;
import com.gruppe10.exercisemanagement.domain.Exercise;
import com.gruppe10.submission.DTOs.ExamDto;
import com.gruppe10.submission.DTOs.ExamSubmissionDto;
import com.gruppe10.submission.DTOs.SubmissionDto;
import com.gruppe10.submission.domain.Answer;
import com.gruppe10.submission.domain.ExamResult;
import com.gruppe10.submission.domain.Submission;
import com.gruppe10.submission.domain.SubmissionStatus;
import com.gruppe10.submission.service.EvaluationService;
import com.gruppe10.submission.service.SubmissionService;
import com.gruppe10.usermanagement.domain.Student;
import com.gruppe10.usermanagement.domain.User;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * ExamExportController.java
 * <p>
 * Created by Fabian Holtapel on 13.05.2025.
 * <p>
 * Description:
 * Controller um Ergebnisse einer Prüfung zu exportieren, alle Exams freizugeben
 */
@RestController
@RequestMapping("/api/student")
@RolesAllowed("STUDENT")
public class ExamExportController {

    private final ExamService examService;
    private final EvaluationService evalService;
    private final SubmissionService submissionService;

    @Autowired
    public ExamExportController(ExamService examService, EvaluationService evalService, SubmissionService submissionService) {
        this.examService = examService;
        this.evalService = evalService;
        this.submissionService = submissionService;
    }

    // Prüfung abrufen A
    @GetMapping("/{examId}")
    public ResponseEntity<ExamDto> getExam(@PathVariable Long examId) {
        Optional<Exam> opt = examService.getById(examId);
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        ExamDto dto = ExamDto.from(opt.get());
        return ResponseEntity.ok(dto);
    }
       /* return examService.getById(examId)
                .map(exam -> ResponseEntity.ok(ExamDto.from(exam)))
                .orElse(ResponseEntity.notFound().build());
    }*/

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

    //Antworten abgeben & auto-graden
    @PostMapping("/{examId}/submit")
    public ResponseEntity<SubmissionDto> submitExam(
            @PathVariable Long examId,
            @RequestBody ExamSubmissionDto payload,
            @AuthenticationPrincipal User user) throws IOException {


        Exam exam = examService.getById(examId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Map<String, Exercise> exerciseMap = exam.getQuestions()
                .stream().collect(Collectors.toMap(ex -> ex.getId().toString(), Function.identity()));

        Map<String, Answer> answers = payload.toDomainAnswers(exerciseMap);

        ExamResult result = evalService.evaluateExam(exam, answers);

        Submission saved = submissionService.bewerten(
                exam,
                user,
                result.getPerQuestionPoints(),
                payload.getRawAnswers()
        );

        return ResponseEntity.ok(SubmissionDto.from(saved));
    }


    @GetMapping("/{examId}/results")
    public ResponseEntity<SubmissionDto> getResults(
            @PathVariable Long examId,
            @AuthenticationPrincipal User user) {

        if (!(user instanceof Student)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Student student = (Student) user;

        Exam exam = examService.getById(examId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Optional<Submission> optionalSub =  submissionService.getSubmissionByStudentAndExam(student, exam);
        if (optionalSub.isEmpty()) {
            System.out .println("--------------->No results found for student " + student.getUsername());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Submission sub = optionalSub.get();

        if (sub.getStatus() == SubmissionStatus.PENDING_REVIEW ||
                (sub.getStatus() == SubmissionStatus.AUTO_GRADED && !sub.getExam().isAutoPublishResults())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Ergebnisse werden erst freigegeben, sobald der Dozent geprüft hat.");
        }

        return ResponseEntity.ok(SubmissionDto.from(sub));
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