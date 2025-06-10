package com.gruppe10.exercisemanagement.domain;

import com.gruppe10.base.domain.AbstractEntity;
import com.gruppe10.examManagement.exam.domain.Exam;
import com.gruppe10.submission.domain.Answer;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "exercise_type", discriminatorType = DiscriminatorType.STRING)
@Table(name = "exercise")
public abstract class Exercise extends AbstractEntity<Long> {

    public static final int TEXT_MAX_LENGTH = 1000;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exercise_id")
    private Long id;

    @Column(name = "score", nullable = false)
    private Long score;

    @Column(name = "exercise_text", length = TEXT_MAX_LENGTH)
    @Size(max = TEXT_MAX_LENGTH)
    private String exerciseText;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "exercise_tag",
            joinColumns = @JoinColumn(name = "exercise_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    @ManyToMany(
            mappedBy = "exercises",
            fetch = FetchType.LAZY
    )
    private List<Exam> exams = new ArrayList<>();



    public abstract double evaluate(Answer answer);

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getScore() {
        return score;
    }

    public void setScore(Long score) {
        this.score = score;
    }

    public String getExerciseText() {
        return exerciseText;
    }

    public void setExerciseText(String exerciseText) {
        this.exerciseText = exerciseText;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public List<Exam> getExam() {
        return exams;
    }

    public void addExam(Exam exam) {
        exam.addExercise(this);
    }
    
}
