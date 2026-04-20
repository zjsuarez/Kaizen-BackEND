package com.kaizen.gym_api.controller;

import com.kaizen.gym_api.dto.response.BodyWeightTrendResponse;
import com.kaizen.gym_api.dto.response.MuscleFrequencyResponse;
import com.kaizen.gym_api.dto.response.OneRepMaxTrendResponse;
import com.kaizen.gym_api.dto.response.RepRangeDistributionResponse;
import com.kaizen.gym_api.dto.response.VolumeTrendResponse;
import com.kaizen.gym_api.dto.response.FatigueCorrelationResponse;
import com.kaizen.gym_api.dto.response.SessionEfficiencyResponse;
import com.kaizen.gym_api.dto.response.RestTimeDistributionResponse;
import com.kaizen.gym_api.dto.response.TrainingActivityResponse;
import com.kaizen.gym_api.dto.response.PrFrequencyResponse;
import com.kaizen.gym_api.dto.response.PrPeakTimeResponse;
import com.kaizen.gym_api.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDate;

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
     * GET /api/statistics/volume?start={date}&end={date}
     *
     * Returns the total volume (weight × reps) lifted by the user,
     * grouped by week (default) or month (if range > 90 days).
     * Date params are optional: omitting them returns all-time data.
     */
    @GetMapping("/volume")
    public ResponseEntity<VolumeTrendResponse> getVolumeTrend(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {

        String email = getAuthenticatedEmail();
        VolumeTrendResponse response = statisticsService.getVolumeTrend(email, start, end);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/statistics/rep-ranges?start={date}&end={date}
     *
     * Analyzes the user's workout sets within a timeframe and groups them
     * into standard fitness rep ranges:
     * - Strength: 1-5 reps
     * - Hypertrophy: 6-12 reps
     * - Endurance: 13+ reps
     * Date params are optional: omitting them returns all-time data.
     */
    @GetMapping("/rep-ranges")
    public ResponseEntity<RepRangeDistributionResponse> getRepRangeDistribution(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {

        String email = getAuthenticatedEmail();
        RepRangeDistributionResponse response = statisticsService.getRepRangeDistribution(email, start, end);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/statistics/muscle-frequency?start={date}&end={date}
     *
     * Counts how many exercises targeting specific muscle groups
     * (e.g., CHEST, BACK, LEGS) the user has performed in a timeframe.
     * Date params are optional: omitting them returns all-time data.
     */
    @GetMapping("/muscle-frequency")
    public ResponseEntity<MuscleFrequencyResponse> getMuscleFrequency(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {

        String email = getAuthenticatedEmail();
        MuscleFrequencyResponse response = statisticsService.getMuscleFrequency(email, start, end);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/statistics/fatigue?start={date}&end={date}
     *
     * Returns the trend of the user's Average RPE alongside their Total Volume over time.
     */
    @GetMapping("/fatigue")
    public ResponseEntity<FatigueCorrelationResponse> getFatigueCorrelation(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {

        String email = getAuthenticatedEmail();
        FatigueCorrelationResponse response = statisticsService.getFatigueCorrelation(email, start, end);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/statistics/efficiency?start={date}&end={date}
     *
     * Returns data points for a Scatter Plot pairing Workout Duration vs Total Volume.
     */
    @GetMapping("/efficiency")
    public ResponseEntity<SessionEfficiencyResponse> getSessionEfficiency(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {

        String email = getAuthenticatedEmail();
        SessionEfficiencyResponse response = statisticsService.getSessionEfficiency(email, start, end);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/statistics/density?start={date}&end={date}
     *
     * Returns a histogram-style distribution of the user's rest times / training density.
     */
    @GetMapping("/density")
    public ResponseEntity<RestTimeDistributionResponse> getRestTimeDistribution(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {

        String email = getAuthenticatedEmail();
        RestTimeDistributionResponse response = statisticsService.getRestTimeDistribution(email, start, end);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/statistics/activity-heatmap?start={date}&end={date}
     *
     * Returns a list of dates representing every day the user has completed a workout,
     * along with the total duration in minutes for each day.
     */
    @GetMapping("/activity-heatmap")
    public ResponseEntity<TrainingActivityResponse> getTrainingActivityHeatmap(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {

        String email = getAuthenticatedEmail();
        TrainingActivityResponse response = statisticsService.getTrainingActivity(email, start, end);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/statistics/pr-heatmap?start={date}&end={date}
     *
     * Returns a list of dates showing when the user hit Personal Records,
     * along with the count of PRs for each day.
     */
    @GetMapping("/pr-heatmap")
    public ResponseEntity<PrFrequencyResponse> getPrFrequencyHeatmap(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {

        String email = getAuthenticatedEmail();
        PrFrequencyResponse response = statisticsService.getPrFrequency(email, start, end);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/statistics/pr-peak-time?start={date}&end={date}
     *
     * Returns the exact time of day (hours and minutes) when the user hits PRs,
     * formatted as data points for a Scatter Chart. Data answers the question: "Am I stronger in the morning or evening?"
     */
    @GetMapping("/pr-peak-time")
    public ResponseEntity<PrPeakTimeResponse> getPrPeakTime(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {

        String email = getAuthenticatedEmail();
        PrPeakTimeResponse response = statisticsService.getPrPeakTime(email, start, end);
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
