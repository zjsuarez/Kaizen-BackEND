package com.kaizen.gym_api.service;

import com.kaizen.gym_api.dto.request.BodyMeasurementRequest;
import com.kaizen.gym_api.dto.response.BodyMeasurementResponse;
import com.kaizen.gym_api.model.BodyMeasurement;
import com.kaizen.gym_api.model.User;
import com.kaizen.gym_api.repository.BodyMeasurementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BodyMeasurementService {

    private final BodyMeasurementRepository repository;

    @Transactional
    public BodyMeasurementResponse logWeight(User user, BodyMeasurementRequest request) {
        LocalDate today = LocalDate.now();
        Optional<BodyMeasurement> existingOpt = repository.findByUserAndRecordedAt(user, today);

        BodyMeasurement measurement;
        if (existingOpt.isPresent()) {
            measurement = existingOpt.get();
            measurement.setWeightKg(request.getWeightKg());
        } else {
            measurement = BodyMeasurement.builder()
                    .user(user)
                    .weightKg(request.getWeightKg())
                    .recordedAt(today)
                    .build();
        }

        BodyMeasurement saved = repository.save(measurement);
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<BodyMeasurementResponse> getWeightHistory(User user) {
        return repository.findByUserOrderByRecordedAtDesc(user).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private BodyMeasurementResponse mapToResponse(BodyMeasurement entity) {
        return BodyMeasurementResponse.builder()
                .id(entity.getId())
                .weightKg(entity.getWeightKg())
                .recordedAt(entity.getRecordedAt())
                .build();
    }
}
