package com.kaizen.gym_api.service.impl;

import com.kaizen.gym_api.dto.response.DashboardResponse;
import com.kaizen.gym_api.dto.response.LastSessionDTO;
import com.kaizen.gym_api.dto.response.NextWorkoutDTO;
import com.kaizen.gym_api.model.Routine;
import com.kaizen.gym_api.model.User;
import com.kaizen.gym_api.model.Workout;
import com.kaizen.gym_api.repository.RoutineRepository;
import com.kaizen.gym_api.repository.UserRepository;
import com.kaizen.gym_api.repository.WorkoutRepository;
import com.kaizen.gym_api.repository.WorkoutSetRepository;
import com.kaizen.gym_api.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final WorkoutRepository workoutRepository;
    private final WorkoutSetRepository workoutSetRepository;
    private final UserRepository userRepository;
    private final RoutineRepository routineRepository;

    @Override
    public DashboardResponse getDashboardMetrics(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        String userId = user.getId();
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

        LastSessionDTO lastSession = buildLastSession(userId);
        NextWorkoutDTO nextWorkout = buildNextWorkout(userId, lastSession);

        // ── KAN-53: Recovery Time & Workout Streak ──
        Integer recoveryTimeHours = calculateRecoveryTimeHours(lastSession);
        Integer workoutStreak = calculateWorkoutStreak(userId);

        return DashboardResponse.builder()
                .totalSessions(totalSessions)
                .avgDurationMinutes(avgDurationMinutes)
                .weeklyVolumeKg(weeklyVolumeKgValue)
                .prsAchieved(prsAchieved)
                .estimated1RM(estimated1RM)
                .lastSession(lastSession)
                .nextWorkout(nextWorkout)
                .recoveryTimeHours(recoveryTimeHours)
                .workoutStreak(workoutStreak)
                .build();
    }

    // Most recently completed workout
    private LastSessionDTO buildLastSession(String userId) {
        Optional<Workout> lastWorkoutOpt = workoutRepository
                .findFirstByUserIdAndEndTimeIsNotNullOrderByEndTimeDesc(userId);

        if (lastWorkoutOpt.isEmpty()) {
            return null;
        }

        Workout lastWorkout = lastWorkoutOpt.get();

        Integer durationMinutes = null;
        if (lastWorkout.getStartTime() != null && lastWorkout.getEndTime() != null) {
            long minutes = ChronoUnit.MINUTES.between(
                    lastWorkout.getStartTime().toLocalDateTime(),
                    lastWorkout.getEndTime().toLocalDateTime());
            durationMinutes = (int) minutes;
        }

        String routineName = null;
        if (lastWorkout.getRoutine() != null) {
            routineName = lastWorkout.getRoutine().getName();
        }

        return LastSessionDTO.builder()
                .workoutId(lastWorkout.getId())
                .routineName(routineName)
                .durationMinutes(durationMinutes)
                .completedAt(lastWorkout.getEndTime().toLocalDateTime())
                .build();
    }

    // Next Workout
    // 1. Last workout had a routine in a plan → pick next routine in plan cycle
    // 2. Fallback: user's active plan → first routine
    // 3. No data → null
    private NextWorkoutDTO buildNextWorkout(String userId, LastSessionDTO lastSession) {
        // Attempt 1: last completed workout's routine
        if (lastSession != null) {
            Optional<Workout> lastWorkoutOpt = workoutRepository
                    .findFirstByUserIdAndEndTimeIsNotNullOrderByEndTimeDesc(userId);

            if (lastWorkoutOpt.isPresent()) {
                Workout lastWorkout = lastWorkoutOpt.get();
                Routine lastRoutine = lastWorkout.getRoutine();

                if (lastRoutine != null && lastRoutine.getPlan() != null) {
                    List<Routine> planRoutines = routineRepository
                            .findByPlan_IdOrderByCreatedAtAsc(lastRoutine.getPlan().getId());

                    if (planRoutines.size() > 1) {
                        int lastIndex = -1;
                        for (int i = 0; i < planRoutines.size(); i++) {
                            if (planRoutines.get(i).getId().equals(lastRoutine.getId())) {
                                lastIndex = i;
                                break;
                            }
                        }

                        if (lastIndex >= 0) {
                            int nextIndex = (lastIndex + 1) % planRoutines.size();
                            Routine nextRoutine = planRoutines.get(nextIndex);
                            return NextWorkoutDTO.builder()
                                    .routineId(nextRoutine.getId())
                                    .routineName(nextRoutine.getName())
                                    .build();
                        }
                    } else if (planRoutines.size() == 1) {
                        Routine onlyRoutine = planRoutines.get(0);
                        return NextWorkoutDTO.builder()
                                .routineId(onlyRoutine.getId())
                                .routineName(onlyRoutine.getName())
                                .build();
                    }
                }
            }
        }

        // Attempt 2: first routine of the user's active training plan
        Optional<Routine> fallbackRoutine = routineRepository
                .findFirstByOwner_IdAndPlan_IsActiveTrueOrderByCreatedAtAsc(userId);

        return fallbackRoutine.map(routine -> NextWorkoutDTO.builder()
                .routineId(routine.getId())
                .routineName(routine.getName())
                .build()).orElse(null);
    }

    // Recovery time - hours since last completed workout
    private Integer calculateRecoveryTimeHours(LastSessionDTO lastSession) {
        if (lastSession == null || lastSession.getCompletedAt() == null) {
            return null;
        }
        long hours = ChronoUnit.HOURS.between(lastSession.getCompletedAt(), LocalDateTime.now());
        return (int) hours;
    }

    // Workout Streak - 96-Hour Rule (based on muscle recovery science)
    private static final long MAX_GAP_HOURS = 96;

    private Integer calculateWorkoutStreak(String userId) {
        List<Timestamp> endTimes = workoutRepository.findCompletedEndTimesByUserId(userId);

        if (endTimes == null || endTimes.isEmpty()) {
            return 0;
        }

        LocalDateTime mostRecent = endTimes.get(0).toLocalDateTime();

        long hoursSinceLast = ChronoUnit.HOURS.between(mostRecent, LocalDateTime.now());
        if (hoursSinceLast > MAX_GAP_HOURS) {
            return 0;
        }

        int streak = 1;
        for (int i = 0; i < endTimes.size() - 1; i++) {
            LocalDateTime newer = endTimes.get(i).toLocalDateTime();
            LocalDateTime older = endTimes.get(i + 1).toLocalDateTime();

            long gap = ChronoUnit.HOURS.between(older, newer);
            if (gap <= MAX_GAP_HOURS) {
                streak++;
            } else {
                break;
            }
        }

        return streak;
    }
}
