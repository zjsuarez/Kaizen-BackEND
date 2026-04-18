package com.kaizen.gym_api.service.impl;

import com.kaizen.gym_api.dto.response.BodyWeightTrendResponse;
import com.kaizen.gym_api.dto.response.MuscleFrequencyResponse;
import com.kaizen.gym_api.dto.response.OneRepMaxTrendResponse;
import com.kaizen.gym_api.dto.response.RepRangeDistributionResponse;
import com.kaizen.gym_api.dto.response.TrendPointDTO;
import com.kaizen.gym_api.dto.response.VolumeTrendResponse;
import com.kaizen.gym_api.dto.response.FatigueCorrelationResponse;
import com.kaizen.gym_api.dto.response.SessionEfficiencyResponse;
import com.kaizen.gym_api.dto.response.RestTimeDistributionResponse;
import com.kaizen.gym_api.model.Exercise;
import com.kaizen.gym_api.model.User;
import com.kaizen.gym_api.repository.BodyMeasurementRepository;
import com.kaizen.gym_api.repository.ExerciseRepository;
import com.kaizen.gym_api.repository.UserRepository;
import com.kaizen.gym_api.repository.WorkoutSetRepository;
import com.kaizen.gym_api.repository.WorkoutSetRepository.RepRangeProjection;
import com.kaizen.gym_api.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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

    /** Threshold in days: if the requested date range exceeds this, group by month instead of week. */
    private static final long MONTHLY_GROUPING_THRESHOLD_DAYS = 90;

    // ──────────────────────────────────────────────────────────────
    // Endpoint: Estimated 1RM Trend
    // ──────────────────────────────────────────────────────────────

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

    // ──────────────────────────────────────────────────────────────
    // Endpoint: Body Weight Trend
    // ──────────────────────────────────────────────────────────────

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

    // ──────────────────────────────────────────────────────────────
    // Endpoint: Weekly / Monthly Volume Trend (Tonnage)
    // ──────────────────────────────────────────────────────────────

    @Override
    public VolumeTrendResponse getVolumeTrend(String email, LocalDate start, LocalDate end) {
        User user = resolveUser(email);
        String userId = user.getId();

        Timestamp startTs = toStartOfDayTimestamp(start);
        Timestamp endTs = toEndOfDayTimestamp(end);

        // Auto-select grouping: MONTHLY if date range > 90 days, WEEKLY otherwise
        String grouping = determineGrouping(start, end);

        List<Object[]> rawTrend;
        if ("MONTHLY".equals(grouping)) {
            rawTrend = workoutSetRepository.findMonthlyVolumeTrend(userId, startTs, endTs);
        } else {
            rawTrend = workoutSetRepository.findWeeklyVolumeTrend(userId, startTs, endTs);
        }

        List<TrendPointDTO> dataPoints = mapToTrendPoints(rawTrend);

        return VolumeTrendResponse.builder()
                .grouping(grouping)
                .dataPoints(dataPoints)
                .build();
    }

    // ──────────────────────────────────────────────────────────────
    // Endpoint: Rep Range Distribution
    // ──────────────────────────────────────────────────────────────

    @Override
    public RepRangeDistributionResponse getRepRangeDistribution(String email, LocalDate start, LocalDate end) {
        User user = resolveUser(email);
        String userId = user.getId();

        Timestamp startTs = toStartOfDayTimestamp(start);
        Timestamp endTs = toEndOfDayTimestamp(end);

        // Query returns a single-row projection; get(0) is safe — aggregate always returns 1 row.
        List<RepRangeProjection> results = workoutSetRepository.findRepRangeDistribution(userId, startTs, endTs);

        long strength = 0L;
        long hypertrophy = 0L;
        long endurance = 0L;

        if (!results.isEmpty()) {
            RepRangeProjection p = results.get(0);
            strength    = p.getStrengthCount()    != null ? p.getStrengthCount()    : 0L;
            hypertrophy = p.getHypertrophyCount() != null ? p.getHypertrophyCount() : 0L;
            endurance   = p.getEnduranceCount()   != null ? p.getEnduranceCount()   : 0L;
        }

        long total = strength + hypertrophy + endurance;

        List<RepRangeDistributionResponse.RepRangeBucket> buckets = new ArrayList<>();
        buckets.add(RepRangeDistributionResponse.RepRangeBucket.builder()
                .category("Strength")
                .range("1-5 reps")
                .count(strength)
                .percentage(safePercentage(strength, total))
                .build());
        buckets.add(RepRangeDistributionResponse.RepRangeBucket.builder()
                .category("Hypertrophy")
                .range("6-12 reps")
                .count(hypertrophy)
                .percentage(safePercentage(hypertrophy, total))
                .build());
        buckets.add(RepRangeDistributionResponse.RepRangeBucket.builder()
                .category("Endurance")
                .range("13+ reps")
                .count(endurance)
                .percentage(safePercentage(endurance, total))
                .build());

        return RepRangeDistributionResponse.builder()
                .totalSets(total)
                .buckets(buckets)
                .build();
    }

    // ──────────────────────────────────────────────────────────────
    // Endpoint: Muscle Group Frequency
    // ──────────────────────────────────────────────────────────────

    @Override
    public MuscleFrequencyResponse getMuscleFrequency(String email, LocalDate start, LocalDate end) {
        User user = resolveUser(email);
        String userId = user.getId();

        Timestamp startTs = toStartOfDayTimestamp(start);
        Timestamp endTs = toEndOfDayTimestamp(end);

        List<Object[]> rawRows = workoutSetRepository.findMuscleFrequency(userId, startTs, endTs);

        // First pass: compute total hits for percentage calculation
        long totalHits = rawRows.stream()
                .mapToLong(row -> row[1] != null ? ((Number) row[1]).longValue() : 0L)
                .sum();

        List<MuscleFrequencyResponse.MuscleHit> muscles = rawRows.stream()
                .map(row -> {
                    String muscleGroup = row[0] != null ? row[0].toString() : "UNKNOWN";
                    long count = row[1] != null ? ((Number) row[1]).longValue() : 0L;
                    return MuscleFrequencyResponse.MuscleHit.builder()
                            .muscleGroup(muscleGroup)
                            .count(count)
                            .percentage(safePercentage(count, totalHits))
                            .build();
                })
                .collect(Collectors.toList());

        return MuscleFrequencyResponse.builder()
                .totalHits(totalHits)
                .muscles(muscles)
                .build();
    }

    // ──────────────────────────────────────────────────────────────
    // Endpoint: Fatigue Correlation (Volume vs RPE)
    // ──────────────────────────────────────────────────────────────

    @Override
    public FatigueCorrelationResponse getFatigueCorrelation(String email, LocalDate start, LocalDate end) {
        User user = resolveUser(email);
        Timestamp startTs = toStartOfDayTimestamp(start);
        Timestamp endTs = toEndOfDayTimestamp(end);

        List<Object[]> rawRows = workoutSetRepository.findFatigueCorrelation(user.getId(), startTs, endTs);
        
        List<FatigueCorrelationResponse.FatiguePoint> points = rawRows.stream()
                .map(row -> FatigueCorrelationResponse.FatiguePoint.builder()
                        .date(convertToLocalDate(row[0]))
                        .totalVolume(convertToDouble(row[1]))
                        .averageRpe(convertToDouble(row[2]))
                        .build())
                .collect(Collectors.toList());

        return FatigueCorrelationResponse.builder()
                .dataPoints(points)
                .build();
    }

    // ──────────────────────────────────────────────────────────────
    // Endpoint: Session Efficiency (Time vs Volume)
    // ──────────────────────────────────────────────────────────────

    @Override
    public SessionEfficiencyResponse getSessionEfficiency(String email, LocalDate start, LocalDate end) {
        User user = resolveUser(email);
        Timestamp startTs = toStartOfDayTimestamp(start);
        Timestamp endTs = toEndOfDayTimestamp(end);

        List<Object[]> rawRows = workoutSetRepository.findSessionEfficiency(user.getId(), startTs, endTs);
        
        List<SessionEfficiencyResponse.ScatterPoint> points = rawRows.stream()
                .map(row -> SessionEfficiencyResponse.ScatterPoint.builder()
                        .durationMinutes(convertToLong(row[0]))
                        .totalVolume(convertToDouble(row[1]))
                        .build())
                .collect(Collectors.toList());

        return SessionEfficiencyResponse.builder()
                .totalSessionsAnalyzed((long) points.size())
                .dataPoints(points)
                .build();
    }

    // ──────────────────────────────────────────────────────────────
    // Endpoint: Rest Time / Density Distribution
    // ──────────────────────────────────────────────────────────────

    @Override
    public RestTimeDistributionResponse getRestTimeDistribution(String email, LocalDate start, LocalDate end) {
        User user = resolveUser(email);
        Timestamp startTs = toStartOfDayTimestamp(start);
        Timestamp endTs = toEndOfDayTimestamp(end);

        List<Object> rawRows = workoutSetRepository.findAverageSecondsPerSet(user.getId(), startTs, endTs);

        long lessThan1Min = 0;
        long oneToTwoMins = 0;
        long twoToThreeMins = 0;
        long threePlusMins = 0;

        for (Object row : rawRows) {
            double seconds = convertToDouble(row);
            if (seconds < 60) {
                lessThan1Min++;
            } else if (seconds < 120) {
                oneToTwoMins++;
            } else if (seconds < 180) {
                twoToThreeMins++;
            } else {
                threePlusMins++;
            }
        }

        long total = lessThan1Min + oneToTwoMins + twoToThreeMins + threePlusMins;

        List<RestTimeDistributionResponse.RestTimeBucket> buckets = new ArrayList<>();
        buckets.add(RestTimeDistributionResponse.RestTimeBucket.builder()
                .category("< 1 min")
                .count(lessThan1Min)
                .percentage(safePercentage(lessThan1Min, total))
                .build());
        buckets.add(RestTimeDistributionResponse.RestTimeBucket.builder()
                .category("1-2 mins")
                .count(oneToTwoMins)
                .percentage(safePercentage(oneToTwoMins, total))
                .build());
        buckets.add(RestTimeDistributionResponse.RestTimeBucket.builder()
                .category("2-3 mins")
                .count(twoToThreeMins)
                .percentage(safePercentage(twoToThreeMins, total))
                .build());
        buckets.add(RestTimeDistributionResponse.RestTimeBucket.builder()
                .category("3+ mins")
                .count(threePlusMins)
                .percentage(safePercentage(threePlusMins, total))
                .build());

        return RestTimeDistributionResponse.builder()
                .totalWorkoutsAnalyzed(total)
                .buckets(buckets)
                .build();
    }

    // ──────────────────────────────────────────────────────────────
    // Private Helpers
    // ──────────────────────────────────────────────────────────────

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

    /**
     * Safely converts various numeric types to long. Handles BigDecimal, Long, Integer, etc.
     */
    private long convertToLong(Object numObj) {
        if (numObj == null) {
            return 0L;
        } else if (numObj instanceof Number) {
            return ((Number) numObj).longValue();
        }
        throw new IllegalStateException("Unexpected numeric type: " + numObj.getClass().getName());
    }

    /**
     * Converts a nullable LocalDate to a Timestamp at start of day (00:00:00).
     * Returns null when input is null so the repository query's IS NULL check applies.
     */
    private Timestamp toStartOfDayTimestamp(LocalDate date) {
        if (date == null) return null;
        return Timestamp.valueOf(date.atStartOfDay());
    }

    /**
     * Converts a nullable LocalDate to a Timestamp at end of day (23:59:59.999).
     * Returns null when input is null so the repository query's IS NULL check applies.
     */
    private Timestamp toEndOfDayTimestamp(LocalDate date) {
        if (date == null) return null;
        return Timestamp.valueOf(date.atTime(LocalTime.MAX));
    }

    /**
     * Determines whether to group volume data WEEKLY or MONTHLY based on the
     * requested date range. Defaults to WEEKLY when no range is specified.
     */
    private String determineGrouping(LocalDate start, LocalDate end) {
        if (start != null && end != null) {
            long days = ChronoUnit.DAYS.between(start, end);
            return days > MONTHLY_GROUPING_THRESHOLD_DAYS ? "MONTHLY" : "WEEKLY";
        }
        return "WEEKLY";
    }

    /**
     * Calculates percentage with null/zero safety. Rounds to 1 decimal place.
     */
    private Double safePercentage(long count, long total) {
        if (total == 0) return 0.0;
        return Math.round((double) count / total * 1000.0) / 10.0;
    }
}

