package com.kaizen.gym_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response DTO for the Rep Range Distribution analysis.
 * Groups the user's workout sets into standard fitness rep ranges
 * and provides both absolute count and percentage for each bucket.
 *
 * Follows the {metadata + dataPoints} analytics wrapper convention,
 * but uses a dedicated RepRangeBucket instead of TrendPointDTO since
 * this is categorical (not time-series) data.
 */
@Data
@Builder
public class RepRangeDistributionResponse {

    /** Total number of qualifying sets analyzed across the timeframe. */
    private Long totalSets;

    @Builder.Default
    private List<RepRangeBucket> buckets = List.of();

    /**
     * A single rep-range category with its absolute count and percentage.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RepRangeBucket {

        /** Category label: "Strength", "Hypertrophy", or "Endurance". */
        private String category;

        /** Rep range description: "1-5 reps", "6-12 reps", or "13+ reps". */
        private String range;

        /** Number of sets in this bucket. */
        private Long count;

        /** Percentage of total sets (0.0 – 100.0). */
        private Double percentage;
    }
}
