package com.kaizen.gym_api.dto.response;

import com.kaizen.gym_api.model.enums.PlanIntervalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainingPlanResponse {
    
    private String id;
    private String name;
    private String description;
    private Date startingDate;
    private PlanIntervalType interval;
    private Integer cycleLength;
    private Boolean isActive;
    private Timestamp createdAt;
}
