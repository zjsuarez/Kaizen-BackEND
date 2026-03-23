package com.kaizen.gym_api.controller;

import com.kaizen.gym_api.dto.request.TrainingPlanRequest;
import com.kaizen.gym_api.dto.response.TrainingPlanResponse;
import com.kaizen.gym_api.service.TrainingPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plans")
@RequiredArgsConstructor
public class TrainingPlanController {

    private final TrainingPlanService trainingPlanService;

    @PostMapping
    public ResponseEntity<TrainingPlanResponse> createPlan(@Valid @RequestBody TrainingPlanRequest request) {
        String userEmail = getAuthenticatedUserEmail();
        TrainingPlanResponse response = trainingPlanService.createPlan(userEmail, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<TrainingPlanResponse>> getAllPlans() {
        String userEmail = getAuthenticatedUserEmail();
        List<TrainingPlanResponse> responseList = trainingPlanService.getAllPlans(userEmail);
        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrainingPlanResponse> getPlanById(@PathVariable("id") String id) {
        String userEmail = getAuthenticatedUserEmail();
        TrainingPlanResponse response = trainingPlanService.getPlanById(id, userEmail);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TrainingPlanResponse> updatePlan(
            @PathVariable("id") String id, 
            @Valid @RequestBody TrainingPlanRequest request) {
        String userEmail = getAuthenticatedUserEmail();
        TrainingPlanResponse response = trainingPlanService.updatePlan(id, userEmail, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlan(@PathVariable("id") String id) {
        String userEmail = getAuthenticatedUserEmail();
        trainingPlanService.deletePlan(id, userEmail);
        return ResponseEntity.noContent().build();
    }

    private String getAuthenticatedUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
