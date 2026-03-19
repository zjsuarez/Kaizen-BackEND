package com.kaizen.gym_api.service;

import com.kaizen.gym_api.dto.request.UpdateUserRequest;
import com.kaizen.gym_api.dto.response.UserResponse;
import com.kaizen.gym_api.model.BodyMeasurement;
import com.kaizen.gym_api.model.User;
import com.kaizen.gym_api.repository.BodyMeasurementRepository;
import com.kaizen.gym_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BodyMeasurementRepository bodyMeasurementRepository;

    public UserResponse updateUser(String userEmail, UpdateUserRequest request) {
        // Find the user making the request
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Check if updating email and it's not already taken
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Email already taken");
            }
            user.setEmail(request.getEmail());
        }

        // Check if updating username and it's not already taken
        if (request.getUsername() != null && !request.getUsername().equals(user.getUsername())) {
            if (userRepository.findByUsername(request.getUsername()).isPresent()) {
                throw new IllegalArgumentException("Username already taken");
            }
            user.setUsername(request.getUsername());
        }

        // Update optional fields
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getProfilePic() != null) {
            user.setProfilePic(request.getProfilePic());
        }
        if (request.getPrimaryGoal() != null) {
            user.setPrimaryGoal(request.getPrimaryGoal());
        }
        if (request.getUnitSystem() != null) {
            user.setUnitSystem(request.getUnitSystem());
        }
        if (request.getEffortMeasurement() != null) {
            user.setEffortMeasurement(request.getEffortMeasurement());
        }
        if (request.getRestTimerDefault() != null) {
            user.setRestTimerDefault(request.getRestTimerDefault());
        }
        if (request.getEquipmentAvailable() != null) {
            user.setEquipmentAvailable(request.getEquipmentAvailable());
        }

        // Save updated user
        User updatedUser = userRepository.save(user);

        // Check if calibration / body measurement data was provided
        if (request.getWeightKg() != null || request.getBodyFatPercentage() != null || request.getProgressPhotoUrl() != null) {
            BodyMeasurement measurement = BodyMeasurement.builder()
                    .user(updatedUser)
                    .weightKg(request.getWeightKg())
                    .bodyFatPercentage(request.getBodyFatPercentage())
                    .progressPhotoUrl(request.getProgressPhotoUrl())
                    .build();
            bodyMeasurementRepository.save(measurement);
        }

        // Return safe DTO
        return UserResponse.builder()
                .id(updatedUser.getId())
                .username(updatedUser.getUsername())
                .email(updatedUser.getEmail())
                .profilePic(updatedUser.getProfilePic())
                .primaryGoal(updatedUser.getPrimaryGoal())
                .unitSystem(updatedUser.getUnitSystem())
                .effortMeasurement(updatedUser.getEffortMeasurement())
                .restTimerDefault(updatedUser.getRestTimerDefault())
                .equipmentAvailable(updatedUser.getEquipmentAvailable())
                .build();
    }
}
