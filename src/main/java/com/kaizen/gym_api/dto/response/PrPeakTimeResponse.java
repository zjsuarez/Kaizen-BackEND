package com.kaizen.gym_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrPeakTimeResponse {

    @Builder.Default
    private Long totalPrsAnalyzed = 0L;

    @Builder.Default
    private List<PrTimePoint> dataPoints = List.of();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PrTimePoint {
        private LocalDate date;
        private LocalTime timeOfDay;
        private Integer hourOfDay;
        private Integer minuteOfHour;
    }
}
