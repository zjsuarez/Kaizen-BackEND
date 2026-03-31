package com.kaizen.gym_api.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NextWorkoutDTO {
    private String routineId;
    private String routineName;
}
