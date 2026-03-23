package com.kaizen.gym_api.repository;

import com.kaizen.gym_api.model.Routine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoutineRepository extends JpaRepository<Routine, String> {
    
    // Find all routines owned by a user
    List<Routine> findByOwner_Email(String email);
    
    // Find all routines owned by a user for a specific plan
    List<Routine> findByOwner_EmailAndPlan_Id(String email, String planId);
    
    // Find a specific routine verifying user ownership
    Optional<Routine> findByIdAndOwner_Email(String id, String email);
}
