/**
 * Author: Christian Markow
 * Date: 29/04/2025
 */

package com.gruppe10.usermanagement.ui.view;

import com.gruppe10.base.ui.Layout.MainLayout;
import com.gruppe10.examManagement.exam.domain.Exam;
import com.gruppe10.examManagement.exam.domain.ExamRepository;
import com.gruppe10.examManagement.examAppointment.domain.StudentExam;
import com.gruppe10.examManagement.examAppointment.domain.StudentExamRepository;
import com.gruppe10.submission.domain.Submission;
import com.gruppe10.submission.domain.SubmissionAnswer;
import com.gruppe10.usermanagement.domain.Student;
import com.gruppe10.usermanagement.domain.StudentRepository;
import com.gruppe10.usermanagement.domain.User;
import com.gruppe10.usermanagement.service.UserService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Route(value = "user-info", layout = MainLayout.class)
@PageTitle("User Info")
@Menu(order = 8, icon = "vaadin:cogs", title = "Profil")
@RolesAllowed({"INSTRUCTOR", "STUDENT"})
public class UserInfoView extends VerticalLayout {

    private final UserService userService;
    private final StudentExamRepository studentExamRepository;

    @Autowired
    private ExamRepository examRepository;

    UserInfoView(UserService userService, StudentExamRepository studentExamRepository, ExamRepository examRepository) {
        this.userService = userService;
        this.studentExamRepository = studentExamRepository;
        this.examRepository = examRepository;
        setPadding(true);
        setSpacing(true);
        setWidthFull();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserDetails userDetails) {
            Optional<User> optionalUser = userService.findByEmail(userDetails.getUsername());
            optionalUser.ifPresent(user -> { initUI(user); });
        } else {
            add(new Div("Fehler beim Laden des Benutzers"));
        }
    }

    private void initUI(User user) {
        add(new H2("Benutzerprofil von " + user.getForename() + " " + user.getSurname()));

        // Gemeinsame Informationen
        FormLayout formLayout = new FormLayout();
        formLayout.addFormItem(new Span(user.getForename() + " " + user.getSurname()), "Name");
        formLayout.addFormItem(new Span("********"), "Passwort");
        formLayout.addFormItem(new Span(user.getEmail()), "E-Mail");
        formLayout.addFormItem(new Button("Passwort ändern", e -> {
            showChangePasswordDialog(user);
        }), "");
        add(formLayout);

        // Rollenspezifische Inhalte
        if ("INSTRUCTOR".equals(user.getRoleAsString())) {
            add(new H3("Verwaltung"));
            add(new Button("Aufgaben", e -> {
                UI.getCurrent().navigate("exercises");
            }));
            add(new Button("Prüfungen", e -> {
                UI.getCurrent().navigate("pruefung-list");
            }));
        } else if ("STUDENT".equals(user.getRoleAsString())) {
            add(new H3("Prüfungshistorie"));

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy").withZone(ZoneId.systemDefault());

            Grid<StudentExam> examGrid = new Grid<>(StudentExam.class, false);
            examGrid.addColumn(exam -> exam.getExam().getTitle()).setHeader("Prüfung");
            examGrid.addColumn(exam -> {
                LocalDateTime submitDate = exam.getEndTime();
                return submitDate != null ? formatter.format(submitDate.atZone(ZoneId.systemDefault())) : "N/A";
            }).setHeader("Prüfungstermin");
            //examGrid.addColumn(exam -> exam.getSubmission().getTotalPoints()).setHeader("Punktzahl");
            examGrid.addColumn(exam -> {
                Submission submission = exam.getSubmission();
                if (submission == null) {
                    return "N/A";
                }
                Boolean passed = submission.getPassed();
                if (passed == null) {
                    return "N/A";
                }
                return passed ? "✔" : "✖";
            }).setHeader("Bestanden");
            examGrid.addColumn(exam -> berechneNote(exam)).setHeader("Note");

            List<StudentExam> examHistory = studentExamRepository.findCompletedExamsByStudent(user)
                    .stream()
                    .sorted(Comparator.comparing(StudentExam::getEndTime, Comparator.nullsLast(Comparator.reverseOrder())))
                    .toList();
            examGrid.setItems(examHistory);

            examGrid.setWidth("90%");
            examGrid.setAllRowsVisible(true);
            add(examGrid);
        }
    }

    private void showChangePasswordDialog(User user) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Passwort ändern");
        dialog.setWidth("70%");
        dialog.setMaxWidth("500px");

        PasswordField oldPasswordField = new PasswordField("Altes Passwort");
        oldPasswordField.setWidthFull();
        oldPasswordField.setRequired(true);
        oldPasswordField.setAutofocus(true);
        PasswordField newPasswordField = new PasswordField("Neues Passwort");
        newPasswordField.setWidthFull();
        newPasswordField.setRequired(true);
        newPasswordField.setMinLength(5);
        PasswordField confirmPasswordField = new PasswordField("Neues Passwort wiederholen");
        confirmPasswordField.setWidthFull();
        confirmPasswordField.setRequired(true);

        Span errorMessage = new Span();
        errorMessage.getStyle().set("color", "red");

        VerticalLayout layout = new VerticalLayout(oldPasswordField, newPasswordField, confirmPasswordField, errorMessage);
        layout.setPadding(false);
        layout.setSpacing(true);
        dialog.add(layout);

        Button saveButton = new Button("Speichern", e -> {
            errorMessage.setText("");
            try {
                userService.changePassword(user, oldPasswordField.getValue(), newPasswordField.getValue(), confirmPasswordField.getValue());
                Notification.show("Passwort erfolgreich geändert", 3000, Notification.Position.BOTTOM_CENTER);
                dialog.close();
            } catch (IllegalArgumentException ex) {
                errorMessage.setText(ex.getMessage());
            }
        });

        Button cancelButton = new Button("Abbrechen", e -> dialog.close());
        dialog.getFooter().add(cancelButton, saveButton);
        dialog.open();
    }

    private String berechneNote(StudentExam exam) {
        Submission submission = exam.getSubmission();
        if (submission == null || submission.getAnswers() == null) return "N/A";

        double erreichtePunkte = submission.getTotalPoints() != null ? submission.getTotalPoints() : 0.0;
        double gesamtpunkte = exam.getExam().getGesamtpunkte();

        double prozent = 100.0 * erreichtePunkte / gesamtpunkte;
        return formatiereNote(prozent);
    }

    private String formatiereNote(double prozent) {
        if (prozent >= 95.0) return "1,0";
        if (prozent >= 90.0) return "1,3";
        if (prozent >= 85.0) return "1,7";
        if (prozent >= 80.0) return "2,0";
        if (prozent >= 75.0) return "2,3";
        if (prozent >= 70.0) return "2,7";
        if (prozent >= 65.0) return "3,0";
        if (prozent >= 60.0) return "3,3";
        if (prozent >= 55.0) return "3,7";
        if (prozent >= 50.0) return "4,0";
        return "5,0";
    }

}