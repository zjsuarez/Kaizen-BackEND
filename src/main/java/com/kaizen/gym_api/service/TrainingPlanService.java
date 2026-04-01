package com.kaizen.gym_api.service;

import com.kaizen.gym_api.dto.request.TrainingPlanRequest;
import com.kaizen.gym_api.dto.response.TrainingPlanResponse;
import com.kaizen.gym_api.model.TrainingPlan;
import com.kaizen.gym_api.model.User;
import com.kaizen.gym_api.repository.TrainingPlanRepository;
import com.kaizen.gym_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrainingPlanService {

    private final TrainingPlanRepository trainingPlanRepository;
    private final UserRepository userRepository;

    public TrainingPlanResponse createPlan(String userEmail, TrainingPlanRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        TrainingPlan plan = TrainingPlan.builder()
                .user(user)
                .name(request.getName())
                .description(request.getDescription())
                .startingDate(request.getStartingDate())
                .interval(request.getInterval())
                .cycleLength(request.getCycleLength())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();

        TrainingPlan savedPlan = trainingPlanRepository.save(plan);
        return mapToResponse(savedPlan);
    }

    public List<TrainingPlanResponse> getAllPlans(String userEmail) {
        return trainingPlanRepository.findByUser_Email(userEmail).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public TrainingPlanResponse getPlanById(String planId, String userEmail) {
        TrainingPlan plan = trainingPlanRepository.findByIdAndUser_Email(planId, userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Training plan not found or you don't have access to it."));
        return mapToResponse(plan);
    }

    public TrainingPlanResponse updatePlan(String planId, String userEmail, TrainingPlanRequest request) {
        TrainingPlan plan = trainingPlanRepository.findByIdAndUser_Email(planId, userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Training plan not found or you don't have access to it."));

        plan.setName(request.getName());
        plan.setDescription(request.getDescription());
        plan.setStartingDate(request.getStartingDate());
        plan.setInterval(request.getInterval());
        plan.setCycleLength(request.getCycleLength());
        if (request.getIsActive() != null) {
            plan.setIsActive(request.getIsActive());
        }

        TrainingPlan updatedPlan = trainingPlanRepository.save(plan);
        return mapToResponse(updatedPlan);
    }

    public void deletePlan(String planId, String userEmail) {
        TrainingPlan plan = trainingPlanRepository.findByIdAndUser_Email(planId, userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Training plan not found or you don't have access to it."));
        trainingPlanRepository.delete(plan);
    }

    private TrainingPlanResponse mapToResponse(TrainingPlan plan) {
        return TrainingPlanResponse.builder()
                .id(plan.getId())
                .name(plan.getName())
                .description(plan.getDescription())
                .startingDate(plan.getStartingDate())
                .interval(plan.getInterval())
                .cycleLength(plan.getCycleLength())
                .isActive(plan.getIsActive())
                .createdAt(plan.getCreatedAt())
                .build();
    }
}
