package com.kaizen.gym_api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "BodyMeasurements")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BodyMeasurement {

    @Id
    @UuidGenerator
    @Column(name = "id_PK", updatable = false, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId_FK", nullable = false)
    private User user;

    @Column(name = "weightKg", precision = 5, scale = 2)
    private BigDecimal weightKg;

    @Column(name = "bodyFatPercentage", precision = 5, scale = 2)
    private BigDecimal bodyFatPercentage;

    @Column(name = "progressPhotoUrl")
    private String progressPhotoUrl;

    @CreationTimestamp
    @Column(name = "date", updatable = false, nullable = false)
    private Timestamp date;
}
