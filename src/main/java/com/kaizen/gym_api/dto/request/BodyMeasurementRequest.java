package com.kaizen.gym_api.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BodyMeasurementRequest {

    @Positive(message = "Weight must be positive")
    private Double weightKg;

    @DecimalMin(value = "0.0", inclusive = true, message = "Body fat percentage must be between 0 and 100")
    @DecimalMax(value = "100.0", inclusive = true, message = "Body fat percentage must be between 0 and 100")
    private Double bodyFatPercentage;

}
