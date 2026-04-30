package com.kaizen.gym_api.dto.response;

import com.kaizen.gym_api.model.enums.AuthProvider;
import com.kaizen.gym_api.model.enums.EquipmentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    private String id;
    private String username;
    private String email;
    private String profilePic;
    private String primaryGoal;
    private Set<EquipmentType> equipmentAvailable;
    private String unitSystem;
    private String effortMeasurement;
    private Integer restTimerDefault;
    private AuthProvider authProvider;

}
