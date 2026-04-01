package com.kaizen.gym_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoutineResponse {

    private String id;
    private String planId; // Can be null
    private String name;
    private String description;
    private String schedulingValue;
    private Date startingDate;
    private Date lastPerformedDate;
    private Timestamp createdAt;

    // A list of nested exercise responses mapped to this routine
    private List<RoutineExerciseResponse> exercises;
}
