package com.kaizen.gym_api.repository;

import com.kaizen.gym_api.model.Workout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkoutRepository extends JpaRepository<Workout, String> {

    List<Workout> findByUser_EmailOrderByStartTimeDesc(String email);

    Optional<Workout> findByIdAndUser_Email(String id, String email);

    long countByUserId(String userId);

    @Query(value = "SELECT AVG(TIMESTAMPDIFF(MINUTE, startTime, endTime)) " +
                   "FROM Workouts " +
                   "WHERE userId_FK = :userId AND endTime IS NOT NULL", nativeQuery = true)
    Double calculateAverageDurationInMinutes(@Param("userId") String userId);
}
