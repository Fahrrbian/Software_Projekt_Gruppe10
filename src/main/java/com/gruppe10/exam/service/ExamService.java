package com.gruppe10.exam.service;

/**
 * Author: Henrik Struckmeier
 * Date: 30/04/2025
 **/



import com.gruppe10.exam.domain.Exam;
import com.gruppe10.exam.domain.ExamRepository;
import com.gruppe10.exam.ui.ExamListener;
import com.gruppe10.submission.domain.Submission;
import com.gruppe10.submission.service.SubmissionService;
import com.gruppe10.usermanagement.domain.User;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.core.support.RepositoryMethodInvocationListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class ExamService {

    private final ExamRepository examRepository;
    private final Clock clock;
    private final RepositoryMethodInvocationListener repositoryMethodInvocationListener;
    private List<ExamListener> listener;
    private SubmissionService submissionService;

    ExamService(ExamRepository examRepository, Clock clock, RepositoryMethodInvocationListener repositoryMethodInvocationListener, SubmissionService submissionService) {
        this.submissionService = submissionService;
        this.examRepository = examRepository;
        this.clock = clock;
        this.repositoryMethodInvocationListener = repositoryMethodInvocationListener;
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

    public Exam getFirst() {
        return examRepository.findAll().get(0);
    }

    public Exam getLast(){
        return examRepository.findAll().get(examRepository.findAll().size()-1);
    }

    public Exam getById(Long id) {
        try{
            return  examRepository.findById(id).get();
        }catch (Exception e) {
            return null;
        }
    }


    public List<Exam> getExamsByCurrentInstructor(User instructor) {
        return examRepository.findByCreator(instructor);
    }
}
