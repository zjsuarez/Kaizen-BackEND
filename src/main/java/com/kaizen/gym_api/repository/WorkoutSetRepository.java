package com.kaizen.gym_api.repository;

import com.kaizen.gym_api.model.WorkoutSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public interface WorkoutSetRepository extends JpaRepository<WorkoutSet, String> {

    List<WorkoutSet> findByWorkout_IdOrderBySetNumberAsc(String workoutId);

    @Query("SELECT MAX(ws.weightKg * ws.reps) FROM WorkoutSet ws " +
           "WHERE ws.workout.user.email = :email AND ws.exercise.id = :exerciseId")
    BigDecimal findMaxVolumeByExerciseAndUser(@Param("email") String email, @Param("exerciseId") String exerciseId);

    @Query("SELECT SUM(ws.weightKg * ws.reps) FROM WorkoutSet ws JOIN ws.workout w " +
           "WHERE w.user.id = :userId AND w.startTime >= :startOfWeek AND w.startTime <= :endOfWeek")
    BigDecimal calculateTotalWeeklyVolume(@Param("userId") String userId,
                                          @Param("startOfWeek") Timestamp startOfWeek,
                                          @Param("endOfWeek") Timestamp endOfWeek);

    @Query("SELECT COUNT(ws) FROM WorkoutSet ws JOIN ws.workout w " +
           "WHERE ws.isPR = true AND w.user.id = :userId")
    long countPrsByUserId(@Param("userId") String userId);
}
