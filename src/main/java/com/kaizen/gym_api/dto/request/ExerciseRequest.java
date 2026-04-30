package com.kaizen.gym_api.dto.request;

import com.kaizen.gym_api.model.enums.ExerciseType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseRequest {

    @NotBlank(message = "Exercise name is required")
    private String name;

    private String description;

    private String muscleTarget;

    private String metrics;

    @NotNull(message = "Exercise type is required")
    private ExerciseType type;
}
