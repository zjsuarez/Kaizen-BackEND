package com.kaizen.gym_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionEfficiencyResponse {

    private Long totalSessionsAnalyzed;

    @Builder.Default
    private List<ScatterPoint> dataPoints = List.of();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScatterPoint {
        private Long durationMinutes;
        private Double totalVolume;
    }
}
