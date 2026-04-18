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
public class RestTimeDistributionResponse {

    private Long totalWorkoutsAnalyzed;

    @Builder.Default
    private List<RestTimeBucket> buckets = List.of();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RestTimeBucket {
        private String category;
        private Long count;
        private Double percentage;
    }
}
