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

@Service
@RequiredArgsConstructor
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;
    private final UserRepository userRepository;

    @Transactional
    public ExerciseResponse createCustomExercise(String email, ExerciseRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

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
        return ExerciseResponse.builder()
                .id(saved.getId())
                .name(saved.getName())
                .description(saved.getDescription())
                .muscleTarget(saved.getMuscleTarget())
                .metrics(saved.getMetrics())
                .type(saved.getType())
                .isCustom(saved.getIsCustom())
                .createdAt(saved.getCreatedAt())
                .build();
    }
}
