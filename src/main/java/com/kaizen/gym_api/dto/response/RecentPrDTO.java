package com.kaizen.gym_api.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class RecentPrDTO {
    private String exerciseName;
    private Double weight;
    private Integer reps;
    private LocalDateTime achievedAt;
}
