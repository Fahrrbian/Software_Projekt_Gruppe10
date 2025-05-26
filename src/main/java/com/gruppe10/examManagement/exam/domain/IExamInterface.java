package com.gruppe10.examManagement.exam.domain;

import org.jspecify.annotations.Nullable;

import java.time.Instant;

public interface IExamInterface {
    //Getter & Setter folgen
    @Nullable
    Long getId();

    String getTitle();

    void setTitle(String title);

    Instant getCreationDate();

    void setCreationDate(Instant creationDate);

    @Nullable
    Long getCreatorId();

    void setCreatorId(Long creatorId);

    double getGesamtpunkte();

    void setGesamtpunkte(double gesamtpunkte);

    double getBestehensgrenze();

    void setBestehensgrenze(double bestehensgrenze);
}
