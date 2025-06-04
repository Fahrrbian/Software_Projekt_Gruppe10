///**
// * Author: Christian Markow
// * Date: 27.05.2025
// */
//
//package com.gruppe10.examManagement.exam.domain;
//
//import com.gruppe10.usermanagement.domain.User;
//import jakarta.persistence.*;
//import java.time.Instant;
//
//@Entity
//@Table(name = "exam_sessions")
//public class ExamSession {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "exam_id", nullable = false)
//    private Exam exam;
//
////    @ManyToOne(fetch = FetchType.EAGER)
////    @JoinColumn(name = "appointment_id", nullable = false)
////    private ExamAppointment appointment;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id", nullable = false)
//    private User user;
//
//    @Column(name = "start_time", nullable = false)
//    private Instant startTime;
//
//    @Column(name = "duration", nullable = false)
//    private long duration;
//
//    @Column(name = "finished", nullable = false)
//    private boolean finished;
//
//    public ExamSession(Exam exam, ExamAppointment appointment, User user, Instant startTime, long duration) {
//        this.exam = exam;
//        this.appointment = appointment;
//        this.user = user;
//        this.startTime = startTime;
//        this.duration = duration;
//        this.finished = false;
//    }
//
//    public Long getId() {
//        return id;
//    }
//
//    public Exam getExam() {
//        return exam;
//    }
//
//    public void setExam(Exam exam) {
//        this.exam = exam;
//    }
//
//    public ExamAppointment getAppointment() {
//        return appointment;
//    }
//
//    public void setAppointment(ExamAppointment appointment) {
//        this.appointment = appointment;
//    }
//
//    public User getUser() {
//        return user;
//    }
//
//    public void setUser(User user) {
//        this.user = user;
//    }
//
//    public Instant getStartTime() {
//        return startTime;
//    }
//
//    public void setStartTime(Instant startTime) {
//        this.startTime = startTime;
//    }
//
//    public long getDuration() {
//        return duration;
//    }
//
//    public void setDuration(long duration) {
//        this.duration = duration;
//    }
//
//    public boolean isFinished() {
//        return finished;
//    }
//
//    public void setFinished(boolean finished) {
//        this.finished = finished;
//    }
//
//}
//
