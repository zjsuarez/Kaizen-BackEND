package com.kaizen.gym_api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoutineExerciseRequest {

    private Integer targetSets;
    private String customExerciseId;
    private String builtinExerciseKey;
}
