package com.kaizen.gym_api.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Response DTO for the Estimated 1RM evolution chart.
 * Contains the exercise name for chart title and an array of
 * date-value data points (best Epley 1RM per day).
 */
@Data
@Builder
public class OneRepMaxTrendResponse {

    private String exerciseName;

    @Builder.Default
    private List<TrendPointDTO> dataPoints = List.of();
}
