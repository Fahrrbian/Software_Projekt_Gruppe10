package com.gruppe10.submission.DTOs;

import java.util.Map;

/**
 * ReviewDto.java
 * <p>
 * Created by Fabian Holtapel on 31.05.2025.
 * <p>
 * Description:
 * Das ReviewDto ist ein Hilfsobjekt mit dem der Instructor die nachträglich korrigierten
 * Punkte (ggf. Aus FT-Answers) vom Frontend an den "Server" übergeben kann.
 */

public class ReviewDto {
    private Map<String, Double> updatedPoints;

    public Map<String, Double> getUpdatedPoints() {
        return updatedPoints;
    }

    public void setUpdatedPoints(Map<String, Double> updatedPoints) {
        this.updatedPoints = updatedPoints;
    }
}
