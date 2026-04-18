package com.kaizen.gym_api.service;

import com.kaizen.gym_api.dto.response.BodyWeightTrendResponse;
import com.kaizen.gym_api.dto.response.MuscleFrequencyResponse;
import com.kaizen.gym_api.dto.response.OneRepMaxTrendResponse;
import com.kaizen.gym_api.dto.response.RepRangeDistributionResponse;
import com.kaizen.gym_api.dto.response.VolumeTrendResponse;

import java.time.LocalDate;

/**
 * Service interface for the Statistics / Analytics Lab module.
 * Provides trend data optimized for mobile line chart plotting.
 */
public interface StatisticsService {

    /**
     * Returns the Estimated 1RM (Epley) progression for a specific exercise over time.
     * Calculates the best set per day and returns date-value pairs.
     *
     * @param email       the authenticated user's email (resolved from JWT)
     * @param exerciseId  UUID of a custom exercise (nullable, mutually exclusive with exerciseKey)
     * @param exerciseKey key of a builtin exercise (nullable, mutually exclusive with exerciseId)
     * @return trend response with exercise name and data points
     */
    OneRepMaxTrendResponse getOneRepMaxTrend(String email, String exerciseId, String exerciseKey);

    /**
     * Returns the user's logged body weight progression over time.
     *
     * @param email the authenticated user's email (resolved from JWT)
     * @return trend response with unit system and data points
     */
    BodyWeightTrendResponse getBodyWeightTrend(String email);

    /**
     * Returns the total volume (weight × reps) lifted by the user, grouped by week or month.
     * Grouping is automatically selected: MONTHLY for ranges > 90 days, WEEKLY otherwise.
     *
     * @param email the authenticated user's email (resolved from JWT)
     * @param start optional start date filter (inclusive)
     * @param end   optional end date filter (inclusive)
     * @return volume trend with grouping metadata and data points
     */
    VolumeTrendResponse getVolumeTrend(String email, LocalDate start, LocalDate end);

    /**
     * Analyzes the user's workout sets within a timeframe and groups them into
     * standard fitness rep ranges: Strength (1-5), Hypertrophy (6-12), Endurance (13+).
     *
     * @param email the authenticated user's email (resolved from JWT)
     * @param start optional start date filter (inclusive)
     * @param end   optional end date filter (inclusive)
     * @return distribution with absolute counts and percentages per category
     */
    RepRangeDistributionResponse getRepRangeDistribution(String email, LocalDate start, LocalDate end);

    /**
     * Counts how many exercise sets target specific muscle groups within a timeframe.
     *
     * @param email the authenticated user's email (resolved from JWT)
     * @param start optional start date filter (inclusive)
     * @param end   optional end date filter (inclusive)
     * @return map of muscle groups to their hit counts and percentages
     */
    MuscleFrequencyResponse getMuscleFrequency(String email, LocalDate start, LocalDate end);
}
