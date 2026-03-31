package com.kaizen.gym_api.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardResponse {
    private Integer totalSessions;
    private Integer avgDurationMinutes;
    private Double weeklyVolumeKg;
    private Integer prsAchieved;
    private Double estimated1RM;
    private LastSessionDTO lastSession;
    private NextWorkoutDTO nextWorkout;
}
