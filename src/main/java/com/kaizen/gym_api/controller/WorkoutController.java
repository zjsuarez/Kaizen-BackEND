package com.kaizen.gym_api.controller;

import com.kaizen.gym_api.dto.request.WorkoutRequest;
import com.kaizen.gym_api.dto.response.WorkoutResponse;
import com.kaizen.gym_api.service.WorkoutService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workouts")
@RequiredArgsConstructor
public class WorkoutController {

    private final WorkoutService workoutService;

    @PostMapping
    public ResponseEntity<WorkoutResponse> createWorkout(@Valid @RequestBody WorkoutRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        WorkoutResponse response = workoutService.createWorkout(email, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<WorkoutResponse>> getAllWorkouts() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        List<WorkoutResponse> responses = workoutService.getAllWorkouts(email);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{workoutId}")
    public ResponseEntity<WorkoutResponse> getWorkoutById(@PathVariable String workoutId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        WorkoutResponse response = workoutService.getWorkoutById(email, workoutId);
        return ResponseEntity.ok(response);
    }
}
