package com.kaizen.gym_api.dto.request;

import com.kaizen.gym_api.model.enums.PlanIntervalType;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainingPlanRequest {

    @NotBlank(message = "Plan name is required")
    private String name;

    private String description;
    private Date startingDate;
    private PlanIntervalType interval;
    private Integer cycleLength;
    
    @Builder.Default
    private Boolean isActive = true;
}
