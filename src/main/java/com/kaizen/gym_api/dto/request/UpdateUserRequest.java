package com.kaizen.gym_api.dto.request;

import com.kaizen.gym_api.model.enums.EquipmentType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequest {

    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @Email(message = "Email should be valid")
    private String email;

    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    private String profilePic;

    @Size(max = 255)
    private String primaryGoal;

    @Size(max = 20)
    private String unitSystem;

    @Size(max = 10)
    private String effortMeasurement;

    private Integer restTimerDefault;

    private Set<EquipmentType> equipmentAvailable;
}
