package com.kaizen.gym_api.dto.response;

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
public class WorkoutSetResponse {

    private String id;
    private String customExerciseId;
    private String builtinExerciseKey;
    private String exerciseName;
    private Integer setNumber;
    private BigDecimal weightKg;
    private Integer reps;
    private Integer rpe;
    private String value;
    private WorkoutSetType type;
    private Boolean isPR;
}
