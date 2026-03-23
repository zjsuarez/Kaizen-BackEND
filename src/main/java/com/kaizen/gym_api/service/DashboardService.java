package com.kaizen.gym_api.service;

import com.kaizen.gym_api.dto.response.DashboardResponse;

public interface DashboardService {
    DashboardResponse getDashboardMetrics(String userId);
}
