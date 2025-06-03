package com.gruppe10.examManagement.exam.service;

/**
 * Author: Henrik Struckmeier
 * Date: 30/04/2025
 **/

import com.gruppe10.examManagement.exam.domain.Exam;
import com.gruppe10.examManagement.exam.domain.ExamRepository;
import com.gruppe10.examManagement.exam.ui.ExamListener;
import com.gruppe10.exercisemanagement.domain.Exercise;
import com.gruppe10.examManagement.examAppointment.domain.ExamAppointment;
import com.gruppe10.examManagement.examAppointment.domain.ExamAppointmentRepository;
import com.gruppe10.exercisemanagement.domain.Answer;
import com.gruppe10.exercisemanagement.domain.AnswerRepository;
import com.gruppe10.exercisemanagement.domain.Exercise;
import com.gruppe10.exercisemanagement.domain.ExerciseRepository;
import com.gruppe10.exercisemanagement.service.AnswerService;
import com.gruppe10.submission.service.SubmissionService;
import com.gruppe10.usermanagement.domain.User;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.core.support.RepositoryMethodInvocationListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class ExamService {

    private final ExamRepository examRepository;
    private final Clock clock;
    private final RepositoryMethodInvocationListener repositoryMethodInvocationListener;
    private List<ExamListener> listener;
    private SubmissionService submissionService;
    private final ExamAppointmentRepository examAppointmentRepository;
    private final ExerciseRepository exerciseRepository;
    private final AnswerService answerService;

    ExamService(ExamRepository examRepository, Clock clock, RepositoryMethodInvocationListener repositoryMethodInvocationListener, SubmissionService submissionService, ExamAppointmentRepository examAppointmentRepository, ExerciseRepository exerciseRepository, AnswerService answerService) {
        this.submissionService = submissionService;
        this.examRepository = examRepository;
        this.clock = clock;
        this.repositoryMethodInvocationListener = repositoryMethodInvocationListener;
        this.examAppointmentRepository = examAppointmentRepository;
        this.exerciseRepository = exerciseRepository;
        this.answerService = answerService;
    }

    //Hier wird ein neues Prüfungsobjekt erstellt und in der Datenbank gespeichert
    public void createPruefung(String title, Long creatorId, @Nullable Long module) {
        if ("fail".equals(title)) {
            throw new RuntimeException("This is for testing the error handler");
        }

        var pruefung = new Exam();
        pruefung.setTitle(title);
        pruefung.setCreationDate(clock.instant());
        pruefung.setModule(module);
        pruefung.setCreatorId(creatorId);
        pruefung.setGesamtpunkte(10.0);
        pruefung.setBestehensgrenze(5.0);
        examRepository.saveAndFlush(pruefung);
    }

    //Hier wird eine Prüfung anhand der Id gelöscht
    public void removePruefung(Long id) {
        if (id == null) {
            throw new RuntimeException("Pruefung id is null");
        }
        List<Exam> liste = examRepository.findAll();
        for (Exam IExamInterface : liste) {
            if (IExamInterface.getId().equals(id)) {
                examRepository.delete(IExamInterface);
                examRepository.flush();
            }
        }
    }

    @Transactional
    public void updatePruefung(Exam IExamInterface, Long id) {
        examRepository.findById(id).ifPresent(pruefung1 -> {
            pruefung1.setTitle(IExamInterface.getTitle());
            if (IExamInterface.getCreationDate() == null) {
                pruefung1.setCreationDate(clock.instant());
            }else {
                pruefung1.setCreationDate(IExamInterface.getCreationDate());}
            pruefung1.setBestehensgrenze(IExamInterface.getBestehensgrenze());
            pruefung1.setCreatorId(IExamInterface.getCreatorId());
        });
        updateListener();
    }
    @Transactional
    public void addExerciseToExam(Long examId, Exercise exercise) {
        Optional<Exam> examOpt = examRepository.findById(examId);
        if (examOpt.isPresent()) {
            Exam exam = examOpt.get();
            exam.addExercise(exercise);
            examRepository.save(exam);
        }
    }



    public List<Exam> list(Pageable pageable) {
        return examRepository.findAllBy(pageable).toList();
    }

    public void startListening(ExamListener examListener) {
        if (listener == null) {
            listener = new ArrayList<>();
        }
        listener.add(examListener);
    }
    private void updateListener() {
        for (ExamListener examListener : listener) {
            examListener.getUpdate();
        }
    }

    public Exam save(Exam exam) {
        return examRepository.save(exam);
    }

    public Exam getFirst() {
        return examRepository.findAll().get(0);
    }

    public Exam getLast(){
        return examRepository.findAll().get(examRepository.findAll().size()-1);
    }

    @Transactional
    public Optional<Exam> getById(Long id) {
        try{
            return  examRepository.findById(id);
        }catch (Exception e) {
            return null;
        }
    }

    public List<Exam> getExamsByCurrentInstructor(User instructor) {
        return examRepository.findByCreator(instructor);
    }

    public void assignExercisesToExam(List<Long> exerciseIds, Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Prüfung nicht gefunden"));

        List<Exercise> exercises = exerciseRepository.findAllById(exerciseIds);
        for (Exercise exercise : exercises) {
            exercise.addExam(exam); // Zuordnung
        }

        exerciseRepository.saveAll(exercises);
    }

    public void submitAnswers(Collection<Answer> answers) {
        answerService.saveAnswers(answers);
    }

    public void saveExamAppointment(ExamAppointment appointment) {
        examAppointmentRepository.save(appointment);
    }

    public List<Exam> getAllExams() {
        return examRepository.findAll();
    }

    public Exam saveExam(Exam exam) {
        return examRepository.save(exam);
    }

    public Optional<Exam> getExamWithExercises(Long id) {
        return examRepository.findByIdWithExercises(id);
    }

}