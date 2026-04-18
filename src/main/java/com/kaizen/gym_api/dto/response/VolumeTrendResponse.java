package com.kaizen.gym_api.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Response DTO for the Weekly/Monthly Volume (Tonnage) trend chart.
 * Contains the grouping granularity used and an array of date-value
 * data points (total weight × reps per period).
 *
 * Follows the {metadata + List<TrendPointDTO>} analytics wrapper convention.
 */
@Data
@Builder
public class VolumeTrendResponse {

    /** The grouping granularity applied: "WEEKLY" or "MONTHLY". */
    private String grouping;

    @Builder.Default
    private List<TrendPointDTO> dataPoints = List.of();
}
