package com.kaizen.gym_api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "BodyMeasurements", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"userId_FK", "recordedAt"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BodyMeasurement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_PK", updatable = false, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId_FK", nullable = false)
    private User user;

    @Column(name = "weightKg", nullable = false)
    private Double weightKg;

    @Column(name = "recordedAt", nullable = false)
    private LocalDate recordedAt;

    @CreationTimestamp
    @Column(name = "createdAt", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updatedAt", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (recordedAt == null) {
            recordedAt = LocalDate.now();
        }
    }
}
