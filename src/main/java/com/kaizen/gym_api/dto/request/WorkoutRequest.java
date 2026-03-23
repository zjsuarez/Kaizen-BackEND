package com.kaizen.gym_api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutRequest {

    // Can be null if it was just an ad-hoc ("empty") workout
    private String routineId;

    private Timestamp startTime;

    private Timestamp endTime;

    private String notes;

    // The single payload includes the history of all performed sets
    private List<WorkoutSetRequest> sets;
}
