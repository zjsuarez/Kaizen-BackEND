package com.kaizen.gym_api.repository;

import com.kaizen.gym_api.model.RoutineExercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoutineExerciseRepository extends JpaRepository<RoutineExercise, String> {
    
    // Fetch all exercises bound to a specific routine, ordered by index
    List<RoutineExercise> findByRoutine_IdOrderByOrderIndexAsc(String routineId);
    
    // Clean up all exercises for a routine (helpful for PUT /replace endpoint logic)
    void deleteByRoutine_Id(String routineId);
}
