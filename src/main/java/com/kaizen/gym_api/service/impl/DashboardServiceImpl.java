package com.kaizen.gym_api.service.impl;

import com.kaizen.gym_api.dto.response.DashboardResponse;
import com.kaizen.gym_api.repository.WorkoutRepository;
import com.kaizen.gym_api.repository.WorkoutSetRepository;
import com.kaizen.gym_api.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final WorkoutRepository workoutRepository;
    private final WorkoutSetRepository workoutSetRepository;

    @Override
    public DashboardResponse getDashboardMetrics(String userId) {
        long totalSessionsLong = workoutRepository.countByUserId(userId);
        Integer totalSessions = (int) totalSessionsLong;

        Double avgDurationDouble = workoutRepository.calculateAverageDurationInMinutes(userId);
        Integer avgDurationMinutes = avgDurationDouble != null ? avgDurationDouble.intValue() : 0;

        LocalDate today = LocalDate.now();
        LocalDateTime startOfWeek = today.with(DayOfWeek.MONDAY).atStartOfDay();
        LocalDateTime endOfWeek = today.with(DayOfWeek.SUNDAY).atTime(23, 59, 59, 999999999);

        BigDecimal weeklyVolumeKg = workoutSetRepository.calculateTotalWeeklyVolume(
                userId,
                Timestamp.valueOf(startOfWeek),
                Timestamp.valueOf(endOfWeek));
        Double weeklyVolumeKgValue = weeklyVolumeKg != null ? weeklyVolumeKg.doubleValue() : 0.0;

        long prsAchievedLong = workoutSetRepository.countPrsByUserId(userId);
        Integer prsAchieved = (int) prsAchievedLong;

        Double estimated1RM = workoutSetRepository.findHighestEstimated1RM(userId);
        if (estimated1RM == null) {
            estimated1RM = 0.0;
        } else {
            estimated1RM = Math.round(estimated1RM * 100.0) / 100.0;
        }

        return DashboardResponse.builder()
                .totalSessions(totalSessions)
                .avgDurationMinutes(avgDurationMinutes)
                .weeklyVolumeKg(weeklyVolumeKgValue)
                .prsAchieved(prsAchieved)
                .estimated1RM(estimated1RM)
                .build();
    }
}
