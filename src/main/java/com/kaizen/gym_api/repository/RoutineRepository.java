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

    // Find all routines in a training plan, ordered by creation date (for cycle logic)
    List<Routine> findByPlan_IdOrderByCreatedAtAsc(String planId);

    // Find the first routine of a user's active training plan (fallback for next workout)
    Optional<Routine> findFirstByOwner_IdAndPlan_IsActiveTrueOrderByCreatedAtAsc(String userId);
}
