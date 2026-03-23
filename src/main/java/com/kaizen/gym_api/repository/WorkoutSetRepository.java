package com.kaizen.gym_api.repository;

import com.kaizen.gym_api.model.WorkoutSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.math.BigDecimal;

public interface WorkoutSetRepository extends JpaRepository<WorkoutSet, String> {

    @Query("SELECT SUM(ws.weightKg * ws.reps) FROM WorkoutSet ws JOIN ws.workout w " +
           "WHERE w.user.id = :userId AND w.startTime >= :startOfWeek AND w.startTime <= :endOfWeek")
    BigDecimal calculateTotalWeeklyVolume(@Param("userId") String userId,
                                          @Param("startOfWeek") Timestamp startOfWeek,
                                          @Param("endOfWeek") Timestamp endOfWeek);

    @Query("SELECT COUNT(ws) FROM WorkoutSet ws JOIN ws.workout w " +
           "WHERE ws.isPR = true AND w.user.id = :userId")
    long countPrsByUserId(@Param("userId") String userId);
}
