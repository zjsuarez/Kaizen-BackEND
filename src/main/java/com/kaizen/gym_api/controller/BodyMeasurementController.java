package com.kaizen.gym_api.controller;

import com.kaizen.gym_api.dto.request.BodyMeasurementRequest;
import com.kaizen.gym_api.dto.response.BodyMeasurementResponse;
import com.kaizen.gym_api.model.User;
import com.kaizen.gym_api.repository.UserRepository;
import com.kaizen.gym_api.service.BodyMeasurementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/measurements")
@RequiredArgsConstructor
public class BodyMeasurementController {

    private final BodyMeasurementService bodyMeasurementService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<BodyMeasurementResponse> logWeight(
            @Valid @RequestBody BodyMeasurementRequest request) {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        return ResponseEntity.ok(bodyMeasurementService.logWeight(user, request));
    }

    @GetMapping
    public ResponseEntity<List<BodyMeasurementResponse>> getWeightHistory() {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return ResponseEntity.ok(bodyMeasurementService.getWeightHistory(user));
    }
}
