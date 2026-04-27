package com.kaizen.gym_api.service;

import com.kaizen.gym_api.dto.request.ExerciseRequest;
import com.kaizen.gym_api.dto.response.ExerciseResponse;
import com.kaizen.gym_api.model.Exercise;
import com.kaizen.gym_api.model.User;
import com.kaizen.gym_api.repository.ExerciseRepository;
import com.kaizen.gym_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;
    private final UserRepository userRepository;

    @Transactional
    public ExerciseResponse createCustomExercise(String email, ExerciseRequest request) {
        User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Exercise exercise = Exercise.builder()
                .name(request.getName().trim())
                .description(request.getDescription())
                .muscleTarget(request.getMuscleTarget())
                .metrics(request.getMetrics())
                .type(request.getType())
                .isCustom(true)
                .createdByUser(user)
                .build();

        Exercise saved = exerciseRepository.save(exercise);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ExerciseResponse> getExercises(String email) {
        userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return exerciseRepository.findAllByCreatedByUser_EmailAndIsCustomTrueOrderByCreatedAtDesc(email)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ExerciseResponse getExerciseById(String email, String exerciseId) {
        userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Exercise exercise = exerciseRepository.findByIdAndCreatedByUser_EmailAndIsCustomTrue(exerciseId, email)
                .orElseThrow(() -> new IllegalArgumentException("Exercise not found or does not belong to user"));

        return toResponse(exercise);
    }

    private ExerciseResponse toResponse(Exercise exercise) {
        return ExerciseResponse.builder()
                .id(exercise.getId())
                .name(exercise.getName())
                .description(exercise.getDescription())
                .muscleTarget(exercise.getMuscleTarget())
                .metrics(exercise.getMetrics())
                .type(exercise.getType())
                .isCustom(exercise.getIsCustom())
                .createdAt(exercise.getCreatedAt())
                .build();
    }
}
