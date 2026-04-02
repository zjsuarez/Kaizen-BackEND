package com.kaizen.gym_api.controller;

import com.kaizen.gym_api.model.User;
import com.kaizen.gym_api.repository.UserRepository;
import com.kaizen.gym_api.service.UserPreferencesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/preferences/dashboard")
@RequiredArgsConstructor
public class UserPreferencesController {

    private final UserPreferencesService userPreferencesService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<String>> getDashboardPreferences() {
        User user = getAuthenticatedUser();
        return ResponseEntity.ok(userPreferencesService.getDashboardPreferences(user));
    }

    @PutMapping
    public ResponseEntity<List<String>> updateDashboardPreferences(@RequestBody List<String> newOrder) {
        User user = getAuthenticatedUser();
        return ResponseEntity.ok(userPreferencesService.updateDashboardPreferences(user, newOrder));
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}
