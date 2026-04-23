package com.kaizen.gym_api.repository;

import com.kaizen.gym_api.model.WorkoutSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkoutSetRepository extends JpaRepository<WorkoutSet, String> {

       // ──────────────────────────────────────────────────────────────
       // JPA Projection Interfaces for Statistics Queries
       // ──────────────────────────────────────────────────────────────

       /**
        * Projection for rep range distribution aggregate.
        * Column aliases in the native SQL must exactly match these getter names
        * (case-insensitive: strengthCount → getStrengthCount).
        */
       interface RepRangeProjection {
              Long getStrengthCount();
              Long getHypertrophyCount();
              Long getEnduranceCount();
       }


       List<WorkoutSet> findByWorkout_IdOrderBySetNumberAsc(String workoutId);

       @Query("SELECT MAX(ws.weightKg * ws.reps) FROM WorkoutSet ws " +
                     "WHERE ws.workout.user.email = :email AND ws.customExercise.id = :exerciseId")
       BigDecimal findMaxVolumeByCustomExerciseAndUser(@Param("email") String email,
                     @Param("exerciseId") String exerciseId);

       @Query("SELECT MAX(ws.weightKg * ws.reps) FROM WorkoutSet ws " +
                     "WHERE ws.workout.user.email = :email AND ws.builtinExerciseKey = :builtinExerciseKey")
       BigDecimal findMaxVolumeByBuiltinExerciseKeyAndUser(@Param("email") String email,
                     @Param("builtinExerciseKey") String builtinExerciseKey);

       @Query("SELECT SUM(ws.weightKg * ws.reps) FROM WorkoutSet ws JOIN ws.workout w " +
                     "WHERE w.user.id = :userId AND w.startTime >= :startOfWeek AND w.startTime <= :endOfWeek")
       BigDecimal calculateTotalWeeklyVolume(@Param("userId") String userId,
                     @Param("startOfWeek") Timestamp startOfWeek,
                     @Param("endOfWeek") Timestamp endOfWeek);

       @Query("SELECT COUNT(ws) FROM WorkoutSet ws JOIN ws.workout w " +
                     "WHERE ws.isPR = true AND w.user.id = :userId")
       long countPrsByUserId(@Param("userId") String userId);

       @Query("SELECT MAX(ws.weightKg * (1 + (ws.reps / 30.0))) FROM WorkoutSet ws JOIN ws.workout w WHERE w.user.id = :userId")
       Double findHighestEstimated1RM(@Param("userId") String userId);

       // Recent PRs: top N sets flagged as PR, ordered by workout endTime descending
       @Query("SELECT ws FROM WorkoutSet ws JOIN ws.workout w " +
                     "WHERE ws.isPR = true AND w.user.id = :userId AND w.endTime IS NOT NULL " +
                     "ORDER BY w.endTime DESC")
       List<WorkoutSet> findRecentPrsByUserId(@Param("userId") String userId, Pageable pageable);

       @Query("SELECT AVG(ws.rpe) FROM WorkoutSet ws WHERE ws.workout.id = :workoutId AND ws.rpe IS NOT NULL")
       Double calculateAverageRpeByWorkoutId(@Param("workoutId") String workoutId);

       // Statistics: 1RM Trend - Custom Exercise (by FK)
       @Query(value = "SELECT CAST(w.endTime AS DATE) AS trendDate, " +
                     "ROUND(MAX(ws.weightKg * (1 + ws.reps / 30.0)), 2) AS estimated1RM " +
                     "FROM WorkoutSets ws " +
                     "JOIN Workouts w ON ws.workoutId_FK = w.id_PK " +
                     "WHERE w.userId_FK = :userId " +
                     "AND ws.customExerciseId_FK = :exerciseId " +
                     "AND w.endTime IS NOT NULL " +
                     "AND ws.weightKg IS NOT NULL AND ws.reps IS NOT NULL AND ws.reps > 0 " +
                     "GROUP BY CAST(w.endTime AS DATE) " +
                     "ORDER BY trendDate ASC", nativeQuery = true)
       List<Object[]> find1RMTrendByCustomExercise(@Param("userId") String userId,
                     @Param("exerciseId") String exerciseId);

       // Statistics: 1RM Trend - Builtin Exercise (by key)
       @Query(value = "SELECT CAST(w.endTime AS DATE) AS trendDate, " +
                     "ROUND(MAX(ws.weightKg * (1 + ws.reps / 30.0)), 2) AS estimated1RM " +
                     "FROM WorkoutSets ws " +
                     "JOIN Workouts w ON ws.workoutId_FK = w.id_PK " +
                     "WHERE w.userId_FK = :userId " +
                     "AND ws.builtinExerciseKey = :exerciseKey " +
                     "AND w.endTime IS NOT NULL " +
                     "AND ws.weightKg IS NOT NULL AND ws.reps IS NOT NULL AND ws.reps > 0 " +
                     "GROUP BY CAST(w.endTime AS DATE) " +
                     "ORDER BY trendDate ASC", nativeQuery = true)
       List<Object[]> find1RMTrendByBuiltinExercise(@Param("userId") String userId,
                     @Param("exerciseKey") String exerciseKey);

       // ──────────────────────────────────────────────────────────────
       // Statistics: Weekly Volume Trend (Tonnage)
       // ──────────────────────────────────────────────────────────────
       @Query(value =
              "SELECT DATE(DATE_ADD(w.endTime, INTERVAL(2 - DAYOFWEEK(w.endTime)) DAY)) AS weekStart, " +
              "ROUND(SUM(ws.weightKg * ws.reps), 2) AS totalVolume " +
              "FROM WorkoutSets ws " +
              "JOIN Workouts w ON ws.workoutId_FK = w.id_PK " +
              "WHERE w.userId_FK = :userId " +
              "AND w.endTime IS NOT NULL " +
              "AND ws.weightKg IS NOT NULL AND ws.reps IS NOT NULL AND ws.reps > 0 " +
              "AND (:startDate IS NULL OR w.endTime >= :startDate) " +
              "AND (:endDate IS NULL OR w.endTime <= :endDate) " +
              "GROUP BY weekStart ORDER BY weekStart ASC", nativeQuery = true)
       List<Object[]> findWeeklyVolumeTrend(@Param("userId") String userId,
              @Param("startDate") Timestamp startDate,
              @Param("endDate") Timestamp endDate);

       // ──────────────────────────────────────────────────────────────
       // Statistics: Monthly Volume Trend (Tonnage)
       // ──────────────────────────────────────────────────────────────
       @Query(value =
              "SELECT DATE(CONCAT(YEAR(w.endTime), '-', LPAD(MONTH(w.endTime), 2, '0'), '-01')) AS monthStart, " +
              "ROUND(SUM(ws.weightKg * ws.reps), 2) AS totalVolume " +
              "FROM WorkoutSets ws " +
              "JOIN Workouts w ON ws.workoutId_FK = w.id_PK " +
              "WHERE w.userId_FK = :userId " +
              "AND w.endTime IS NOT NULL " +
              "AND ws.weightKg IS NOT NULL AND ws.reps IS NOT NULL AND ws.reps > 0 " +
              "AND (:startDate IS NULL OR w.endTime >= :startDate) " +
              "AND (:endDate IS NULL OR w.endTime <= :endDate) " +
              "GROUP BY YEAR(w.endTime), MONTH(w.endTime) ORDER BY monthStart ASC", nativeQuery = true)
       List<Object[]> findMonthlyVolumeTrend(@Param("userId") String userId,
              @Param("startDate") Timestamp startDate,
              @Param("endDate") Timestamp endDate);

       // ──────────────────────────────────────────────────────────────
       // Statistics: Rep Range Distribution (single-row aggregate)
       // JPA maps named SQL columns to projection getter names automatically.
       // ──────────────────────────────────────────────────────────────
       @Query(value =
              "SELECT " +
              "SUM(CASE WHEN ws.reps BETWEEN 1 AND 5 THEN 1 ELSE 0 END) AS strengthCount, " +
              "SUM(CASE WHEN ws.reps BETWEEN 6 AND 12 THEN 1 ELSE 0 END) AS hypertrophyCount, " +
              "SUM(CASE WHEN ws.reps >= 13 THEN 1 ELSE 0 END) AS enduranceCount " +
              "FROM WorkoutSets ws " +
              "JOIN Workouts w ON ws.workoutId_FK = w.id_PK " +
              "WHERE w.userId_FK = :userId " +
              "AND w.endTime IS NOT NULL " +
              "AND ws.reps IS NOT NULL AND ws.reps > 0 " +
              "AND (:startDate IS NULL OR w.endTime >= :startDate) " +
              "AND (:endDate IS NULL OR w.endTime <= :endDate)", nativeQuery = true)
       List<RepRangeProjection> findRepRangeDistribution(@Param("userId") String userId,
              @Param("startDate") Timestamp startDate,
              @Param("endDate") Timestamp endDate);

       // ──────────────────────────────────────────────────────────────
       // Statistics: Muscle Group Frequency
       //
       // COALESCE-based Query: for each WorkoutSet, Exercises is joined via
       // LEFT JOINs on either the custom FK OR the builtin key.
       // ──────────────────────────────────────────────────────────────
       @Query(value =
              "SELECT COALESCE(e1.muscleTarget, e2.muscleTarget) AS muscleTarget, COUNT(*) AS hitCount " +
              "FROM WorkoutSets ws " +
              "JOIN Workouts w ON ws.workoutId_FK = w.id_PK " +
              "LEFT JOIN Exercises e1 ON ws.customExerciseId_FK = e1.id_PK " +
              "LEFT JOIN Exercises e2 ON ws.builtinExerciseKey = e2.name AND e2.isCustom = false " +
              "WHERE w.userId_FK = :userId " +
              "AND w.endTime IS NOT NULL " +
              "AND COALESCE(e1.muscleTarget, e2.muscleTarget) IS NOT NULL " +
              "AND (:startDate IS NULL OR w.endTime >= :startDate) " +
              "AND (:endDate IS NULL OR w.endTime <= :endDate) " +
              "GROUP BY COALESCE(e1.muscleTarget, e2.muscleTarget) " +
              "ORDER BY hitCount DESC", nativeQuery = true)
       List<Object[]> findMuscleFrequency(@Param("userId") String userId,
              @Param("startDate") Timestamp startDate,
              @Param("endDate") Timestamp endDate);

       // ──────────────────────────────────────────────────────────────
       // Statistics: Fatigue Correlation (Volume vs RPE)
       // ──────────────────────────────────────────────────────────────
       @Query(value =
              "SELECT CAST(w.endTime AS DATE) AS trendDate, " +
              "ROUND(SUM(ws.weightKg * ws.reps), 2) AS totalVolume, " +
              "ROUND(AVG(ws.rpe), 2) AS averageRpe " +
              "FROM WorkoutSets ws " +
              "JOIN Workouts w ON ws.workoutId_FK = w.id_PK " +
              "WHERE w.userId_FK = :userId " +
              "AND w.endTime IS NOT NULL " +
              "AND ws.weightKg IS NOT NULL AND ws.reps IS NOT NULL AND ws.reps > 0 AND ws.rpe IS NOT NULL " +
              "AND (:startDate IS NULL OR w.endTime >= :startDate) " +
              "AND (:endDate IS NULL OR w.endTime <= :endDate) " +
              "GROUP BY CAST(w.endTime AS DATE) " +
              "ORDER BY trendDate ASC", nativeQuery = true)
       List<Object[]> findFatigueCorrelation(@Param("userId") String userId,
              @Param("startDate") Timestamp startDate,
              @Param("endDate") Timestamp endDate);

       // ──────────────────────────────────────────────────────────────
       // Statistics: Session Efficiency (Time vs Volume)
       // ──────────────────────────────────────────────────────────────
       @Query(value =
              "SELECT TIMESTAMPDIFF(MINUTE, w.startTime, w.endTime) AS durationMinutes, " +
              "ROUND(SUM(ws.weightKg * ws.reps), 2) AS totalVolume " +
              "FROM Workouts w " +
              "JOIN WorkoutSets ws ON ws.workoutId_FK = w.id_PK " +
              "WHERE w.userId_FK = :userId " +
              "AND w.startTime IS NOT NULL AND w.endTime IS NOT NULL " +
              "AND ws.weightKg IS NOT NULL AND ws.reps IS NOT NULL AND ws.reps > 0 " +
              "AND (:startDate IS NULL OR w.endTime >= :startDate) " +
              "AND (:endDate IS NULL OR w.endTime <= :endDate) " +
              "GROUP BY w.id_PK " +
              "ORDER BY w.endTime ASC", nativeQuery = true)
       List<Object[]> findSessionEfficiency(@Param("userId") String userId,
              @Param("startDate") Timestamp startDate,
              @Param("endDate") Timestamp endDate);

       // ──────────────────────────────────────────────────────────────
       // Statistics: Rest Time / Density Distribution
       // ──────────────────────────────────────────────────────────────
       @Query(value =
              "SELECT TIMESTAMPDIFF(SECOND, w.startTime, w.endTime) / COUNT(ws.id_PK) AS avgSecondsPerSet " +
              "FROM Workouts w " +
              "JOIN WorkoutSets ws ON ws.workoutId_FK = w.id_PK " +
              "WHERE w.userId_FK = :userId " +
              "AND w.startTime IS NOT NULL AND w.endTime IS NOT NULL " +
              "AND (:startDate IS NULL OR w.endTime >= :startDate) " +
              "AND (:endDate IS NULL OR w.endTime <= :endDate) " +
              "GROUP BY w.id_PK " +
              "HAVING COUNT(ws.id_PK) > 0", nativeQuery = true)
       List<Object> findAverageSecondsPerSet(@Param("userId") String userId,
              @Param("startDate") Timestamp startDate,
              @Param("endDate") Timestamp endDate);

       // ──────────────────────────────────────────────────────────────
       // Statistics: PR Frequency Heatmap
       // ──────────────────────────────────────────────────────────────
       @Query(value =
              "SELECT CAST(w.endTime AS DATE) AS prDate, COUNT(ws.id_PK) AS prCount " +
              "FROM WorkoutSets ws " +
              "JOIN Workouts w ON ws.workoutId_FK = w.id_PK " +
              "WHERE w.userId_FK = :userId AND w.endTime IS NOT NULL " +
              "AND ws.isPR = true " +
              "AND (:startDate IS NULL OR w.endTime >= :startDate) " +
              "AND (:endDate IS NULL OR w.endTime <= :endDate) " +
              "GROUP BY CAST(w.endTime AS DATE) " +
              "ORDER BY prDate ASC", nativeQuery = true)
       List<Object[]> findPrFrequencyHeatmap(@Param("userId") String userId,
              @Param("startDate") Timestamp startDate,
              @Param("endDate") Timestamp endDate);

       // ──────────────────────────────────────────────────────────────
       // Statistics: PR Peak Time Scatter Plot
       // ──────────────────────────────────────────────────────────────
       @Query(value =
              "SELECT CAST(w.endTime AS DATE) AS prDate, " +
              "w.endTime AS exactTime " +
              "FROM WorkoutSets ws " +
              "JOIN Workouts w ON ws.workoutId_FK = w.id_PK " +
              "WHERE w.userId_FK = :userId AND w.endTime IS NOT NULL " +
              "AND ws.isPR = true " +
              "AND (:startDate IS NULL OR w.endTime >= :startDate) " +
              "AND (:endDate IS NULL OR w.endTime <= :endDate) " +
              "ORDER BY exactTime ASC", nativeQuery = true)
       List<Object[]> findPrPeakTime(@Param("userId") String userId,
              @Param("startDate") Timestamp startDate,
              @Param("endDate") Timestamp endDate);
}
