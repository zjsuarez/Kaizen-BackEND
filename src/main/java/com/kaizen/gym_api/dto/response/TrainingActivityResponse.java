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
public class TrainingActivityResponse {

    @Builder.Default
    private Long totalActiveDays = 0L;

    @Builder.Default
    private List<ActivityPoint> dataPoints = List.of();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ActivityPoint {
        private LocalDate date;
        private Long durationMinutes;
    }
}
