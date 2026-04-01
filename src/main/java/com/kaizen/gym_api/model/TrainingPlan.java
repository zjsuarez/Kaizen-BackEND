package com.kaizen.gym_api.model;

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
@Table(name = "TrainingPlans")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainingPlan {

    @Id
    @UuidGenerator
    @Column(name = "id_PK", updatable = false, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId_FK", nullable = false)
    private User user;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "startingDate")
    private Date startingDate;

    @Column(name = "isActive")
    @Builder.Default
    private Boolean isActive = true;

    // Escaped because Interval is a SQL reserved keyword.
    @Column(name = "`Interval`")
    private String interval;

    @CreationTimestamp
    @Column(name = "createdAt", updatable = false, nullable = false)
    private Timestamp createdAt;
}
