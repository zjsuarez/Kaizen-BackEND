package com.kaizen.gym_api.repository;

import com.kaizen.gym_api.model.WorkoutSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;

public interface WorkoutSetRepository extends JpaRepository<WorkoutSet, String> {

       @Query("SELECT SUM(ws.weightKg * ws.reps) FROM WorkoutSet ws JOIN ws.workout w " +
                     "WHERE w.user.id = :userId AND w.startTime >= :startOfWeek AND w.startTime <= :endOfWeek")
       Double calculateTotalWeeklyVolume(@Param("userId") String userId,
                     @Param("startOfWeek") Timestamp startOfWeek,
                     @Param("endOfWeek") Timestamp endOfWeek);

       @Query("SELECT COUNT(ws) FROM WorkoutSet ws JOIN ws.workout w " +
                     "WHERE ws.isPR = true AND w.user.id = :userId")
       long countPrsByUserId(@Param("userId") String userId);

    @Query("SELECT MAX(ws.weightKg * (1 + (ws.reps / 30.0))) FROM WorkoutSet ws JOIN ws.workout w WHERE w.user.id = :userId")
    Double findHighestEstimated1RM(@Param("userId") String userId);
}
