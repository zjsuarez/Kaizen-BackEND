package com.kaizen.gym_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BodyMeasurementResponse {

    private String id;
    private BigDecimal weightKg;
    private BigDecimal bodyFatPercentage;
    private String progressPhotoUrl;
    private Timestamp date;
}
