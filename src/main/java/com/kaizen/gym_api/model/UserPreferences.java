package com.kaizen.gym_api.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "UserPreferences")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferences {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_PK", updatable = false, nullable = false)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId_FK", nullable = false, unique = true, columnDefinition = "CHAR(36)")
    private User user;

    @ElementCollection
    @CollectionTable(name = "UserPreferenceDashboardWidgets", joinColumns = @JoinColumn(name = "preferencesId_FK"))
    @OrderColumn(name = "widgetOrder")
    @Column(name = "widgetKey", nullable = false)
    @Builder.Default
    private List<String> dashboardWidgets = new ArrayList<>();
}
