package com.kaizen.gym_api.dto.request;

import com.kaizen.gym_api.model.enums.WorkoutSetType;
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

    private String customExerciseId;
    private String builtinExerciseKey;

    private Integer setNumber;

    private BigDecimal weightKg;

    private Integer reps;

    private Integer rpe;

    private String value;

    private WorkoutSetType type;
}
