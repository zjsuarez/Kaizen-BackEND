package com.kaizen.gym_api.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Response DTO for the Body Weight progression chart.
 * Contains the user's unit system and an array of
 * date-value data points (one per measurement entry).
 */
@Data
@Builder
public class BodyWeightTrendResponse {

    private String unit;

    @Builder.Default
    private List<TrendPointDTO> dataPoints = List.of();
}
