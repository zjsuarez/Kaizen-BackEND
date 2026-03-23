package com.kaizen.gym_api.repository;

import com.kaizen.gym_api.model.TrainingPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainingPlanRepository extends JpaRepository<TrainingPlan, String> {

    // Find all training plans belonging to a specific user by their email
    List<TrainingPlan> findByUser_Email(String email);

    // Find a specific training plan by ID and ensure it belongs to the user with the given email
    Optional<TrainingPlan> findByIdAndUser_Email(String id, String email);
}
