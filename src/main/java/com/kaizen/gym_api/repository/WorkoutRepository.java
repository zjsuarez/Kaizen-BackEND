package com.kaizen.gym_api.repository;

import com.kaizen.gym_api.model.Workout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WorkoutRepository extends JpaRepository<Workout, String> {

    long countByUserId(String userId);

    @Query(value = "SELECT AVG(TIMESTAMPDIFF(MINUTE, startTime, endTime)) " +
                   "FROM Workouts " +
                   "WHERE userId_FK = :userId AND endTime IS NOT NULL", nativeQuery = true)
    Double calculateAverageDurationInMinutes(@Param("userId") String userId);
}
