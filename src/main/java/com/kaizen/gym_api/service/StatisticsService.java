package com.kaizen.gym_api.service;

import com.kaizen.gym_api.dto.response.BodyWeightTrendResponse;
import com.kaizen.gym_api.dto.response.OneRepMaxTrendResponse;

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
}
