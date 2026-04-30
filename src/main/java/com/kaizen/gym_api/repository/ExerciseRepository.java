package com.kaizen.gym_api.repository;

import com.kaizen.gym_api.model.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, String> {
	Optional<Exercise> findByIdAndCreatedByUser_EmailAndIsCustomTrue(String id, String email);
	List<Exercise> findAllByCreatedByUser_EmailAndIsCustomTrueOrderByCreatedAtDesc(String email);
}
