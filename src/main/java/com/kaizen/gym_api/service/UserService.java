package com.kaizen.gym_api.service;

import com.kaizen.gym_api.dto.request.UpdateUserRequest;
import com.kaizen.gym_api.dto.response.UserResponse;
import com.kaizen.gym_api.model.User;
import com.kaizen.gym_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponse getUserProfile(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .profilePic(user.getProfilePic())
                .primaryGoal(user.getPrimaryGoal())
                .unitSystem(user.getUnitSystem())
                .effortMeasurement(user.getEffortMeasurement())
                .restTimerDefault(user.getRestTimerDefault())
                .equipmentAvailable(user.getEquipmentAvailable())
                .build();
    }

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
