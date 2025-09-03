//package com.gruppe10.testdata;
//
//import com.gruppe10.examManagement.exam.domain.Exam;
//import com.gruppe10.examManagement.exam.domain.ExamRepository;
//import com.gruppe10.examManagement.examAppointment.domain.StudentExam;
//import com.gruppe10.exercisemanagement.domain.*;
//import com.gruppe10.usermanagement.domain.*;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//
//import java.time.Instant;
//import java.time.LocalDateTime;
//import java.time.temporal.ChronoUnit;
//import java.util.List;
//
//
///**
// * TestDataLoader.java
// * <p>
// * Created by Fabian Holtapel on 09.06.2025.
// * <p>
// * Description:
// * TODO: Beschreibung einfügen.
// */
//@Component
//public class TestDataLoader implements CommandLineRunner {
//
//    private final StudentRepository studentRepo;
//    private final ExamRepository examRepo;
//    private final ExerciseRepository exerciseRepo;
//    private final ChoiceOptionRepository choiceRepo;
//    private final AssignmentPairRepository assignRepo;
//    private final ExamRepository appointmentRepo;
//    private final PasswordEncoder passwordEncoder;
//    private final InstructorRepository instructorRepo;
//
//    public TestDataLoader(StudentRepository studentRepo,
//                          ExamRepository examRepo,
//                          ExerciseRepository exerciseRepo,
//                          ChoiceOptionRepository choiceRepo,
//                          AssignmentPairRepository assignRepo,
//                          ExamRepository appointmentRepo, PasswordEncoder passwordEncoder, InstructorRepository instructorRepo) {
//        this.studentRepo = studentRepo;
//        this.examRepo = examRepo;
//        this.exerciseRepo = exerciseRepo;
//        this.choiceRepo = choiceRepo;
//        this.assignRepo = assignRepo;
//        this.appointmentRepo = appointmentRepo;
//        this.passwordEncoder = passwordEncoder;
//        this.instructorRepo = instructorRepo;
//    }
//
//    @Override
//    public void run(String... args) throws Exception {
//        /*if (examRepo.count() > 0) {
//            System.out.println("Testdaten bereits geladen – übersprunge Loader.");
//            return;
//        }*/
//        Exam exam = new Exam();
//        exam.setTitle("Beispielprüfung SoSe25");
//        exam.setCreationDate(Instant.now());
//        exam.setAppointmentDate(Instant.now().plus(1, ChronoUnit.DAYS)); // Terminfeld füllen
//
//
//
//
//// 2) Studierende anlegen
//        String studentEmail = "max.mustermann@example.com";
//        Student s1 = studentRepo.findByEmail(studentEmail)
//                .orElseGet(() -> {
//                    Student neu = new Student();
//                    neu.setForename("Max");
//                    neu.setSurname("Mustermann");
//                    neu.setEmail(studentEmail);
//                    neu.setPassword(passwordEncoder.encode("geheimesPasswort"));
//                    neu.setStudentNumber(123456);
//                    neu.setRole(Role.STUDENT);
//                    return studentRepo.save(neu);
//                });
//
//        // Gleiches für Instructor
//        String instructorEmail = "lehrer@example.com";
//        Instructor i1 = instructorRepo.findByEmail(instructorEmail)
//                .orElseGet(() -> {
//                    Instructor neu = new Instructor();
//                    neu.setForename("Lehrer");
//                    neu.setSurname("Mustermann");
//                    neu.setEmail(instructorEmail);
//                    neu.setPassword(passwordEncoder.encode("instrPass"));
//                    neu.setRole(Role.INSTRUCTOR);
//                    return instructorRepo.save(neu);
//                });
//
//// 3) Verknüpfe StudentExam (Vorabend-Logik aus deiner Entität)
//        StudentExam se = new StudentExam();
//        se.setExam(exam);
//        se.setStudent(s1);
//        se.setVorname(s1.getForename());
//        se.setNachname(s1.getSurname());
//        se.setMatrikelnummer(String.valueOf(s1.getStudentNumber()));
//        se.setStartTime(LocalDateTime.now());
//        se.setGesperrt(false);
//        se.setCompleted(false);
//
//// 4) Zum Exam hinzufügen und abspeichern
//        exam.addStudentExamAppointment(se);
//        examRepo.save(exam);
//
//        Exercise sc = new SingleChoice();
//        sc.setExerciseText("2+2 = ?");
//        sc.setScore(1L);
//        exerciseRepo.save(sc);
//        choiceRepo.saveAll(List.of(
//                new ChoiceOption("3", false, sc),
//                new ChoiceOption("4", true,  sc),
//                new ChoiceOption("5", false, sc)
//        ));
//        exam.addExercise(sc);
//// Multiple-Choice
//        Exercise mc = new MultipleChoice();
//        mc.setExerciseText("Welche sind gerade?");
//        mc.setScore(2L);
//        exerciseRepo.save(mc);
//        choiceRepo.saveAll(List.of(
//                new ChoiceOption("1", false, mc),
//                new ChoiceOption("2", true,  mc),
//                new ChoiceOption("3", false, mc),
//                new ChoiceOption("4", true,  mc)
//        ));
//        exam.addExercise(mc);
//// Freitext
//        Exercise ft = new FreetextExercise();
//        ft.setExerciseText("Erkläre MVC.");
//        ft.setScore(5L);
//        exerciseRepo.save(ft);
//
//// Zuordnungs-Aufgabe
//        AssignmentExercise  ae = new AssignmentExercise();
//        ae.setExerciseText("Ordne zu:");
//        ae.setScore(3L);
//        exerciseRepo.save(ae);
//
//// dann paare erzeugen und bidirektional verknüpfen
//        AssignmentPair p1 = new AssignmentPair("Model", "Datenhaltung");
//        ae.addAssignmentPair(p1);
//        AssignmentPair p2 = new AssignmentPair("View", "Darstellung");
//        ae.addAssignmentPair(p2);
//        AssignmentPair p3 = new AssignmentPair("Controller", "Logik");
//        ae.addAssignmentPair(p3);
//// Jetzt das ae speichern – dabei werden durch Cascade auch die Pairs mitgespeichert
//
//
//        exerciseRepo.save(ae);
//        exam.addExercise(ae);
//
//        examRepo.save(exam);
//
//// Log-Ausgabe
//        System.out.println("=== Testdaten vollständig angelegt: Prüfung #"
//                + exam.getId() + " mit "
//                + exam.getExercises().size()
//                + " Aufgaben ===");
//    }
//
//}
