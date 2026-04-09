package com.kaizen.gym_api.service;

import com.kaizen.gym_api.dto.request.BodyMeasurementRequest;
import com.kaizen.gym_api.dto.response.BodyMeasurementResponse;
import com.kaizen.gym_api.model.BodyMeasurement;
import com.kaizen.gym_api.model.User;
import com.kaizen.gym_api.repository.BodyMeasurementRepository;
import com.kaizen.gym_api.service.storage.DigitalOceanSpacesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BodyMeasurementService {

    private final BodyMeasurementRepository repository;
    private final DigitalOceanSpacesService digitalOceanSpacesService;

    @Transactional
    public BodyMeasurementResponse logWeight(User user, BodyMeasurementRequest request, MultipartFile progressPhoto) {
        LocalDate today = LocalDate.now();

        boolean hasWeight = request.getWeightKg() != null;
        boolean hasBodyFat = request.getBodyFatPercentage() != null;
        boolean hasPhoto = progressPhoto != null && !progressPhoto.isEmpty();

        if (!hasWeight && !hasBodyFat && !hasPhoto) {
            throw new IllegalArgumentException("At least one field is required: weightKg, bodyFatPercentage or progressPhoto");
        }

        String progressPhotoUrl = hasPhoto
                ? digitalOceanSpacesService.uploadProgressPhoto(user.getId(), progressPhoto)
                : null;

        BodyMeasurement measurement = BodyMeasurement.builder()
                .user(user)
                .weightKg(hasWeight ? request.getWeightKg() : null)
                .bodyFatPercentage(hasBodyFat ? request.getBodyFatPercentage() : null)
                .progressPhotoUrl(progressPhotoUrl)
                .recordedAt(today)
                .build();

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
                .bodyFatPercentage(entity.getBodyFatPercentage())
                .progressPhotoUrl(entity.getProgressPhotoUrl())
                .recordedAt(entity.getRecordedAt())
                .build();
    }
}
