package com.kaizen.gym_api.controller;

import com.kaizen.gym_api.dto.request.RoutineExerciseRequest;
import com.kaizen.gym_api.dto.request.RoutineRequest;
import com.kaizen.gym_api.dto.response.RoutineResponse;
import com.kaizen.gym_api.service.RoutineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routines")
@RequiredArgsConstructor
public class RoutineController {

    private final RoutineService routineService;

    @PostMapping
    public ResponseEntity<RoutineResponse> createRoutine(@Valid @RequestBody RoutineRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        RoutineResponse response = routineService.createRoutine(email, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<RoutineResponse>> getRoutines(@RequestParam(required = false) String planId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        List<RoutineResponse> responses = routineService.getRoutines(email, planId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{routineId}")
    public ResponseEntity<RoutineResponse> getRoutineById(@PathVariable String routineId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        RoutineResponse response = routineService.getRoutineById(email, routineId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{routineId}")
    public ResponseEntity<RoutineResponse> updateRoutine(
            @PathVariable String routineId,
            @Valid @RequestBody RoutineRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        RoutineResponse response = routineService.updateRoutine(email, routineId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{routineId}")
    public ResponseEntity<Void> deleteRoutine(@PathVariable String routineId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        routineService.deleteRoutine(email, routineId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{routineId}/exercises")
    public ResponseEntity<RoutineResponse> updateRoutineExercises(
            @PathVariable String routineId,
            @Valid @RequestBody List<RoutineExerciseRequest> requests) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        RoutineResponse response = routineService.updateRoutineExercises(email, routineId, requests);
        return ResponseEntity.ok(response);
    }
}
