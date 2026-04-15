package com.kaizen.gym_api.service.impl;

import com.kaizen.gym_api.dto.response.BodyWeightTrendResponse;
import com.kaizen.gym_api.dto.response.OneRepMaxTrendResponse;
import com.kaizen.gym_api.dto.response.TrendPointDTO;
import com.kaizen.gym_api.model.Exercise;
import com.kaizen.gym_api.model.User;
import com.kaizen.gym_api.repository.BodyMeasurementRepository;
import com.kaizen.gym_api.repository.ExerciseRepository;
import com.kaizen.gym_api.repository.UserRepository;
import com.kaizen.gym_api.repository.WorkoutSetRepository;
import com.kaizen.gym_api.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticsServiceImpl implements StatisticsService {

    private final UserRepository userRepository;
    private final WorkoutSetRepository workoutSetRepository;
    private final BodyMeasurementRepository bodyMeasurementRepository;
    private final ExerciseRepository exerciseRepository;

    // Endpoint: Estimated 1RM Trend

    @Override
    public OneRepMaxTrendResponse getOneRepMaxTrend(String email, String exerciseId, String exerciseKey) {
        User user = resolveUser(email);
        String userId = user.getId();

        // Validate exactly one identifier must be provided
        if ((exerciseId == null || exerciseId.isBlank()) && (exerciseKey == null || exerciseKey.isBlank())) {
            throw new IllegalArgumentException("Either 'exerciseId' or 'exerciseKey' must be provided");
        }

        String exerciseName;
        List<Object[]> rawTrend;

        if (exerciseId != null && !exerciseId.isBlank()) {
            // Custom exercise path
            Exercise exercise = exerciseRepository.findById(exerciseId)
                    .orElseThrow(() -> new IllegalArgumentException("Exercise not found with id: " + exerciseId));
            exerciseName = exercise.getName();
            rawTrend = workoutSetRepository.find1RMTrendByCustomExercise(userId, exerciseId);
        } else {
            // Builtin exercise path - the key itself serves as the display name
            exerciseName = exerciseKey;
            rawTrend = workoutSetRepository.find1RMTrendByBuiltinExercise(userId, exerciseKey);
        }

        List<TrendPointDTO> dataPoints = mapToTrendPoints(rawTrend);

        return OneRepMaxTrendResponse.builder()
                .exerciseName(exerciseName)
                .dataPoints(dataPoints)
                .build();
    }

    // Endpoint: Body Weight Trend

    @Override
    public BodyWeightTrendResponse getBodyWeightTrend(String email) {
        User user = resolveUser(email);
        String userId = user.getId();

        List<Object[]> rawTrend = bodyMeasurementRepository.findWeightTrendByUserId(userId);
        List<TrendPointDTO> dataPoints = mapToTrendPoints(rawTrend);

        // Include the user's unit system so the mobile chart can render the correct
        // Y-axis label
        String unit = user.getUnitSystem() != null ? user.getUnitSystem() : "KG";

        return BodyWeightTrendResponse.builder()
                .unit(unit)
                .dataPoints(dataPoints)
                .build();
    }

    // Private Helpers

    /**
     * Resolves email (from JWT principal) to User entity.
     * Follows the Email → UUID resolution pattern mandated by agent.md.
     */
    private User resolveUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found for email: " + email));
    }

    /**
     * Maps raw Object[] rows (date, value) from lightweight projections
     * into typed TrendPointDTO instances for JSON serialization.
     * Handles both java.sql.Date (native queries) and LocalDate (JPQL) return
     * types.
     */
    private List<TrendPointDTO> mapToTrendPoints(List<Object[]> rawRows) {
        if (rawRows == null || rawRows.isEmpty()) {
            return Collections.emptyList();
        }

        return rawRows.stream()
                .map(row -> {
                    LocalDate date = convertToLocalDate(row[0]);
                    Double value = convertToDouble(row[1]);
                    return TrendPointDTO.builder()
                            .date(date)
                            .value(value)
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * Safely converts various date types returned by JPA/native queries to
     * LocalDate.
     * Native MySQL queries return java.sql.Date; JPQL may return LocalDate
     * directly.
     */
    private LocalDate convertToLocalDate(Object dateObj) {
        if (dateObj instanceof LocalDate) {
            return (LocalDate) dateObj;
        } else if (dateObj instanceof java.sql.Date) {
            return ((java.sql.Date) dateObj).toLocalDate();
        } else if (dateObj instanceof java.sql.Timestamp) {
            return ((java.sql.Timestamp) dateObj).toLocalDateTime().toLocalDate();
        }
        throw new IllegalStateException("Unexpected date type: " + dateObj.getClass().getName());
    }

    /**
     * Safely converts various numeric types (BigDecimal, Double, etc.) to Double.
     */
    private Double convertToDouble(Object numObj) {
        if (numObj == null) {
            return 0.0;
        } else if (numObj instanceof Double) {
            return (Double) numObj;
        } else if (numObj instanceof Number) {
            return ((Number) numObj).doubleValue();
        }
        throw new IllegalStateException("Unexpected numeric type: " + numObj.getClass().getName());
    }
}
