package com.kaizen.gym_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response DTO for the Muscle Group Frequency analysis.
 * Counts how many exercise instances (sets) target each muscle group
 * within a given timeframe.
 *
 * Follows the {metadata + dataPoints} analytics wrapper convention,
 * using a dedicated MuscleHit inner class for the categorical data.
 */
@Data
@Builder
public class MuscleFrequencyResponse {

    /** Total exercise hits across all muscle groups in the timeframe. */
    private Long totalHits;

    @Builder.Default
    private List<MuscleHit> muscles = List.of();

    /**
     * A single muscle group with its hit count.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MuscleHit {

        /** Muscle group name (e.g. "CHEST", "BACK", "LEGS"). */
        private String muscleGroup;

        /** Number of sets targeting this muscle group. */
        private Long count;

        /** Percentage of total hits (0.0 – 100.0). */
        private Double percentage;
    }
}
