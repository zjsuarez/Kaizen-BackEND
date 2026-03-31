package com.kaizen.gym_api.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class LastSessionDTO {
    private String workoutId;
    private String routineName;
    private Integer durationMinutes;
    private LocalDateTime completedAt;
}
