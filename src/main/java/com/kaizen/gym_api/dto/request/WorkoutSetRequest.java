package com.kaizen.gym_api.dto.request;

import com.kaizen.gym_api.model.enums.WorkoutSetType;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutSetRequest {

    @NotBlank(message = "Exercise ID is mandatory")
    private String exerciseId;

    private Integer setNumber;

    private BigDecimal weightKg;

    private Integer reps;

    private Integer rpe;

    private WorkoutSetType type;
}
