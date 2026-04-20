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
public class PrFrequencyResponse {

    @Builder.Default
    private Long totalPrDays = 0L;

    @Builder.Default
    private List<PrFrequencyPoint> dataPoints = List.of();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PrFrequencyPoint {
        private LocalDate date;
        private Long count;
    }
}
