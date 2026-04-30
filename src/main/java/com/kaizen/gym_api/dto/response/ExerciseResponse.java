package com.kaizen.gym_api.dto.response;

import com.kaizen.gym_api.model.enums.ExerciseType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseResponse {

    private String id;
    private String name;
    private String description;
    private String muscleTarget;
    private String metrics;
    private ExerciseType type;
    private Boolean isCustom;
    private Timestamp createdAt;
}
