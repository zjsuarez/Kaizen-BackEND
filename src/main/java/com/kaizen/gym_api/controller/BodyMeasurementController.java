package com.kaizen.gym_api.controller;

import com.kaizen.gym_api.dto.request.BodyMeasurementRequest;
import com.kaizen.gym_api.dto.response.BodyMeasurementResponse;
import com.kaizen.gym_api.service.BodyMeasurementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users/me/body-measurements")
@RequiredArgsConstructor
public class BodyMeasurementController {

    private final BodyMeasurementService bodyMeasurementService;

    @PostMapping
    public ResponseEntity<BodyMeasurementResponse> createMeasurement(@Valid @RequestBody BodyMeasurementRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        BodyMeasurementResponse response = bodyMeasurementService.createMeasurement(email, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<BodyMeasurementResponse>> getMeasurements() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        List<BodyMeasurementResponse> responses = bodyMeasurementService.getMeasurements(email);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/latest")
    public ResponseEntity<BodyMeasurementResponse> getLatestMeasurement() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        BodyMeasurementResponse response = bodyMeasurementService.getLatestMeasurement(email);
        return ResponseEntity.ok(response);
    }
}
