package com.kaizen.gym_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoutineExerciseRequest {

    @NotBlank(message = "Exercise ID is required")
    private String exerciseId;

    private Integer targetSets;

    private Integer targetReps;

    private Integer restSeconds;
}
