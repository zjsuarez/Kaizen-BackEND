package com.kaizen.gym_api.model;

import com.kaizen.gym_api.model.enums.ExerciseType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.sql.Timestamp;

@Entity
@Table(name = "Exercises")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Exercise {

    @Id
    @UuidGenerator
    @Column(name = "id_PK", updatable = false, nullable = false)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "muscleTarget")
    private String muscleTarget;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private ExerciseType type;

    @Column(name = "isCustom")
    @Builder.Default
    private Boolean isCustom = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createdByUserId_FK")
    private User createdByUser;

    @CreationTimestamp
    @Column(name = "createdAt", updatable = false, nullable = false)
    private Timestamp createdAt;
}
