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
}
