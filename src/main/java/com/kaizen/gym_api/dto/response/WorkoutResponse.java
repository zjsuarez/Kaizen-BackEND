package com.kaizen.gym_api.dto.response;

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
public class WorkoutResponse {

    private String id;
    private String routineId;
    private String routineName; // Nullable
    private Timestamp startTime;
    private Timestamp endTime;
    private String notes;

    // Populated nested sets
    private List<WorkoutSetResponse> sets;
}
