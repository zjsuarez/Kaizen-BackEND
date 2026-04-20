package com.kaizen.gym_api.repository;

import com.kaizen.gym_api.model.Workout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkoutRepository extends JpaRepository<Workout, String> {

        List<Workout> findByUser_EmailOrderByStartTimeDesc(String email);

        Optional<Workout> findByIdAndUser_Email(String id, String email);

        long countByUserId(String userId);

        Optional<Workout> findFirstByUserIdAndEndTimeIsNotNullOrderByEndTimeDesc(String userId);

        // Lightweight projection: only endTime timestamps for streak calculation (no
        // full entities)
        @Query("SELECT w.endTime FROM Workout w WHERE w.user.id = :userId AND w.endTime IS NOT NULL ORDER BY w.endTime DESC")
        List<Timestamp> findCompletedEndTimesByUserId(@Param("userId") String userId);

        @Query(value = "SELECT AVG(TIMESTAMPDIFF(MINUTE, startTime, endTime)) " +
                        "FROM Workouts " +
                        "WHERE userId_FK = :userId AND endTime IS NOT NULL", nativeQuery = true)
        Double calculateAverageDurationInMinutes(@Param("userId") String userId);

        // Calendar: distinct completed-workout dates for a given month (lightweight
        // projection)
        @Query(value = "SELECT DISTINCT CAST(endTime AS DATE) FROM Workouts " +
                        "WHERE userId_FK = :userId AND endTime IS NOT NULL " +
                        "AND YEAR(endTime) = :year AND MONTH(endTime) = :month " +
                        "ORDER BY CAST(endTime AS DATE) ASC", nativeQuery = true)
        List<LocalDate> findTrainingDaysByUserIdAndMonth(
                        @Param("userId") String userId,
                        @Param("year") int year,
                        @Param("month") int month);

        // Statistics: Training Activity Heatmap
        @Query(value = "SELECT CAST(w.endTime AS DATE) AS activityDate, " +
                        "SUM(TIMESTAMPDIFF(MINUTE, w.startTime, w.endTime)) AS durationMinutes " +
                        "FROM Workouts w " +
                        "WHERE w.userId_FK = :userId AND w.endTime IS NOT NULL AND w.startTime IS NOT NULL " +
                        "AND (:startDate IS NULL OR w.endTime >= :startDate) " +
                        "AND (:endDate IS NULL OR w.endTime <= :endDate) " +
                        "GROUP BY CAST(w.endTime AS DATE) " +
                        "ORDER BY activityDate ASC", nativeQuery = true)
        List<Object[]> findTrainingActivityHeatmap(@Param("userId") String userId,
                        @Param("startDate") Timestamp startDate,
                        @Param("endDate") Timestamp endDate);
}
