package com.kaizen.gym_api.dto.request;

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
public class RoutineRequest {
    
    // Optional, if routine is bound to a training plan
    private String planId;

    @NotBlank(message = "Name is required")
    private String name;

    private String description;
    
    private String schedulingValue;
    
    private Date startingDate;
}
