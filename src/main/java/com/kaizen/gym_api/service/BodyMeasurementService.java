package com.kaizen.gym_api.service;

import com.kaizen.gym_api.dto.request.BodyMeasurementRequest;
import com.kaizen.gym_api.dto.response.BodyMeasurementResponse;
import com.kaizen.gym_api.model.BodyMeasurement;
import com.kaizen.gym_api.model.User;
import com.kaizen.gym_api.repository.BodyMeasurementRepository;
import com.kaizen.gym_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BodyMeasurementService {

    private final BodyMeasurementRepository bodyMeasurementRepository;
    private final UserRepository userRepository;

    public BodyMeasurementResponse createMeasurement(String email, BodyMeasurementRequest request) {
        if (request.getWeightKg() == null && request.getBodyFatPercentage() == null && request.getProgressPhotoUrl() == null) {
            throw new IllegalArgumentException("At least one measurement field must be provided");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        BodyMeasurement measurement = BodyMeasurement.builder()
                .user(user)
                .weightKg(request.getWeightKg())
                .bodyFatPercentage(request.getBodyFatPercentage())
                .progressPhotoUrl(request.getProgressPhotoUrl())
                .build();

        BodyMeasurement saved = bodyMeasurementRepository.save(measurement);
        return mapToResponse(saved);
    }

    public List<BodyMeasurementResponse> getMeasurements(String email) {
        return bodyMeasurementRepository.findByUser_EmailOrderByDateDesc(email).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public BodyMeasurementResponse getLatestMeasurement(String email) {
        BodyMeasurement latest = bodyMeasurementRepository.findFirstByUser_EmailOrderByDateDesc(email)
                .orElseThrow(() -> new IllegalArgumentException("No body measurements found"));
        return mapToResponse(latest);
    }

    private BodyMeasurementResponse mapToResponse(BodyMeasurement measurement) {
        return BodyMeasurementResponse.builder()
                .id(measurement.getId())
                .weightKg(measurement.getWeightKg())
                .bodyFatPercentage(measurement.getBodyFatPercentage())
                .progressPhotoUrl(measurement.getProgressPhotoUrl())
                .date(measurement.getDate())
                .build();
    }
}
