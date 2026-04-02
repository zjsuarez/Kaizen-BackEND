package com.kaizen.gym_api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BodyMeasurementRequest {

    private BigDecimal weightKg;

    private BigDecimal bodyFatPercentage;

    private String progressPhotoUrl;
}
