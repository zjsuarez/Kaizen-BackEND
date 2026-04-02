package com.kaizen.gym_api.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

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
    private Integer recoveryTimeHours;
    private Integer workoutStreak;
    private Double currentWeight;
    private Double weightDiff;
    @Builder.Default
    private List<LocalDate> trainingDaysThisMonth = List.of();
    @Builder.Default
    private List<RecentPrDTO> recentPrs = List.of();
}
