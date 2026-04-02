package com.kaizen.gym_api.service;

import com.kaizen.gym_api.model.User;
import com.kaizen.gym_api.model.UserPreferences;
import com.kaizen.gym_api.repository.UserPreferencesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserPreferencesService {

    private static final List<String> DEFAULT_DASHBOARD_WIDGETS = List.of("NEXT_WORKOUT", "WEIGHT_TREND");

    private final UserPreferencesRepository userPreferencesRepository;

    @Transactional(readOnly = true)
    public List<String> getDashboardPreferences(User user) {
        return userPreferencesRepository.findByUser(user)
                .map(UserPreferences::getDashboardWidgets)
                .filter(widgets -> widgets != null && !widgets.isEmpty())
                .map(ArrayList::new)
                .orElseGet(() -> new ArrayList<>(DEFAULT_DASHBOARD_WIDGETS));
    }

    @Transactional
    public List<String> updateDashboardPreferences(User user, List<String> newOrder) {
        List<String> sanitizedOrder = newOrder == null ? List.of() : newOrder;

        UserPreferences preferences = userPreferencesRepository.findByUser(user)
                .orElseGet(() -> UserPreferences.builder().user(user).build());

        preferences.setDashboardWidgets(new ArrayList<>(sanitizedOrder));
        UserPreferences saved = userPreferencesRepository.save(preferences);
        return new ArrayList<>(saved.getDashboardWidgets());
    }
}
