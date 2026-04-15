package com.kaizen.gym_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Generic date-value pair optimized for mobile line chart plotting.
 * Reusable across all analytics trend endpoints (1RM, body weight, volume, etc.).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrendPointDTO {

    private LocalDate date;
    private Double value;
}
