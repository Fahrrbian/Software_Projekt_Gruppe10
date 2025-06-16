/**
 * Author: Christian Markow
 * Date: 04.06.2025
 */

package com.gruppe10.examManagement.exam.ui.ListView;

import com.gruppe10.base.ui.Layout.MainLayout;
import com.gruppe10.examManagement.exam.domain.Exam;
import com.gruppe10.examManagement.exam.domain.ExamRepository;
import com.gruppe10.examManagement.exam.service.ExamService;
import com.gruppe10.examManagement.examAppointment.domain.StudentExam;
import com.gruppe10.examManagement.examAppointment.domain.StudentExamRepository;
import com.gruppe10.usermanagement.domain.Student;
import com.gruppe10.usermanagement.domain.User;
import com.gruppe10.usermanagement.service.StudentService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import jakarta.annotation.security.RolesAllowed;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Clock;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;

import static com.gruppe10.base.ui.security.SecurityUtils.getCurrentUser;

@Route(value = "student-pruefung-list", layout = MainLayout.class)
@PageTitle("Offene Prüfungen")
@RolesAllowed("STUDENT")
public class StudentExamListView extends VerticalLayout {

    private final ExamService examService;
    private final StudentExamRepository studentExamRepository;
    private final ExamRepository examRepository;
    private final StudentService studentService;
    private final Grid<StudentExam> examGrid;

    public StudentExamListView(ExamService examService, Clock clock, StudentExamRepository studentExamRepository, ExamRepository examRepository, StudentService studentService) {
        this.examService = examService;
        this.studentExamRepository = studentExamRepository;
        this.examRepository = examRepository;
        this.studentService = studentService;

        setSizeFull();
        addClassNames(LumoUtility.BoxSizing.BORDER, LumoUtility.Display.FLEX, LumoUtility.FlexDirection.COLUMN,
                LumoUtility.Padding.MEDIUM, LumoUtility.Gap.SMALL);

        add(new H2("Offene Prüfungen"));

        examGrid = new Grid<>(StudentExam.class, false);
        examGrid.setSizeFull();

        var dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                .withLocale(Locale.getDefault())
                .withZone(clock.getZone());

        //examGrid.addColumn(studentExam -> studentExam.getExam().getId()).setHeader("ID").setAutoWidth(true);
        examGrid.addColumn(studentExam -> studentExam.getExam().getTitle()).setHeader("Titel").setAutoWidth(true);
        examGrid.addColumn(studentExam ->
                dateTimeFormatter.format(studentExam.getExam().getCreationDate())
        ).setHeader("Erstellt am").setAutoWidth(true);

        //Zuweisung von nicht gesperrten Prüfungen an Prüfling und Erzeugung sowie Anzeige von nutzerspezifischen Prüfungen
        examGrid.setItems(query -> {

            Optional<User> currentUser = getCurrentUser();

            if (currentUser.isPresent() && currentUser.get() instanceof Student student) {

                int studentNumber = student.getStudentNumber();
                String studentNrString = String.valueOf(studentNumber);

                List<StudentExam> exams = studentExamRepository.findByMatrikelnummerAndGesperrtFalse(studentNrString);

                return exams.stream()
                        .skip(query.getOffset())
                        .limit(query.getLimit());
            }
            return Stream.empty();
        });

//            int page = query.getOffset() / query.getLimit();
//
//            Optional<User> currentUser = getCurrentUser();
//            if (currentUser.isPresent() && currentUser.get() instanceof Student student) {
//
//                List<Exam> openExams = examService.findByGesperrtFalsePaged(page, query.getLimit()).getContent();
//
//                for (Exam exam : openExams) {
//                    Optional<StudentExam> existing = studentExamRepository.findByStudent_IdAndExam_Id(student.getId(), exam.getId());
//                    if (existing.isEmpty()) {
//                        StudentExam se = new StudentExam();
//                        se.setExam(exam);
//                        se.setStudent(student);
//                        se.setVorname(student.getForename());
//                        se.setNachname(student.getSurname());
//                        se.setMatrikelnummer(String.valueOf(student.getStudentNumber()));
//                        se.setGesperrt(false);
//                        studentExamRepository.save(se);
//                    }
//                }
//
//                Pageable pageable = PageRequest.of(page, query.getLimit());
//                return studentExamRepository.findByStudentAndGesperrtFalse(currentUser, pageable).stream();
//            }
//
//            return Stream.empty();
//        });

        examGrid.addItemDoubleClickListener(event -> {
            if (event.getItem() != null) {
                Long examId = event.getItem().getExam().getId();
                UI.getCurrent().navigate("exam/" + examId);
            }
        });

        add(examGrid);
    }

}