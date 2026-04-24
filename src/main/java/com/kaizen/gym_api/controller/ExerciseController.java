package com.kaizen.gym_api.controller;

import com.kaizen.gym_api.dto.request.ExerciseRequest;
import com.kaizen.gym_api.dto.response.ExerciseResponse;
import com.kaizen.gym_api.service.ExerciseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/exercises")
@RequiredArgsConstructor
public class ExerciseController {

    private final ExerciseService exerciseService;

    @PostMapping
    public ResponseEntity<ExerciseResponse> createCustomExercise(@Valid @RequestBody ExerciseRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        ExerciseResponse response = exerciseService.createCustomExercise(email, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
