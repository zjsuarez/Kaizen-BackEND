package com.kaizen.gym_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoutineExerciseResponse {

    private String id;
    private Integer orderIndex;
    private Integer targetSets;
}
