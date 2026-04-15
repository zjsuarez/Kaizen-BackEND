package com.kaizen.gym_api.controller;

import com.kaizen.gym_api.dto.response.BodyWeightTrendResponse;
import com.kaizen.gym_api.dto.response.OneRepMaxTrendResponse;
import com.kaizen.gym_api.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for the Statistics / Analytics Lab module.
 * Serves lightweight chart data optimized for mobile line chart rendering.
 *
 * All endpoints require JWT authentication.
 * The userId is always extracted from the JWT - never from the request body.
 */
@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    /**
     * GET /api/statistics/1rm?exerciseId={uuid} - 1RM trend for a custom exercise
     * GET /api/statistics/1rm?exerciseKey={key} - 1RM trend for a builtin exercise
     *
     * Returns the best Estimated 1RM (Epley formula) per day for the specified
     * exercise.
     * Exactly one of exerciseId or exerciseKey must be provided.
     */
    @GetMapping("/1rm")
    public ResponseEntity<OneRepMaxTrendResponse> getOneRepMaxTrend(
            @RequestParam(required = false) String exerciseId,
            @RequestParam(required = false) String exerciseKey) {

        String email = getAuthenticatedEmail();
        OneRepMaxTrendResponse response = statisticsService.getOneRepMaxTrend(email, exerciseId, exerciseKey);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/statistics/body-weight
     *
     * Returns the user's logged body weight progression over time,
     * ordered chronologically (oldest → newest) for chart plotting.
     */
    @GetMapping("/body-weight")
    public ResponseEntity<BodyWeightTrendResponse> getBodyWeightTrend() {

        String email = getAuthenticatedEmail();
        BodyWeightTrendResponse response = statisticsService.getBodyWeightTrend(email);
        return ResponseEntity.ok(response);
    }

    /**
     * Extracts the authenticated user's email from the SecurityContext.
     * authentication.getName() returns email per Spring Security + JWT config.
     */
    private String getAuthenticatedEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
