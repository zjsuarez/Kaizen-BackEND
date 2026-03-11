package com.kaizen.gym_api.model;

import com.kaizen.gym_api.model.enums.SchedulingType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.sql.Date;
import java.sql.Timestamp;

@Entity
@Table(name = "Routines")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Routine {

    @Id
    @UuidGenerator
    @Column(name = "id_PK", updatable = false, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ownerId_FK", nullable = false)
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "planId_FK")
    private TrainingPlan plan;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "schedulingType")
    private SchedulingType schedulingType;

    @Column(name = "cycleLength")
    private Integer cycleLength;

    @Column(name = "schedulingValue")
    private String schedulingValue;

    @Column(name = "startingDate")
    private Date startingDate;

    @Column(name = "lastPerformedDate")
    private Date lastPerformedDate;

    @CreationTimestamp
    @Column(name = "createdAt", updatable = false, nullable = false)
    private Timestamp createdAt;
}
