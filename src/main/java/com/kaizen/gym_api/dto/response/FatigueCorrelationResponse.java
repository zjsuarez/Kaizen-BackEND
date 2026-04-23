package com.kaizen.gym_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FatigueCorrelationResponse {
    
    @Builder.Default
    private List<FatiguePoint> dataPoints = List.of();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FatiguePoint {
        private LocalDate date;
        private Double totalVolume;
        private Double averageRpe;
    }
}
