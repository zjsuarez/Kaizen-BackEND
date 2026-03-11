package com.kaizen.gym_api.model;

import com.kaizen.gym_api.model.enums.WorkoutSetType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;

@Entity
@Table(name = "WorkoutSets")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutSet {

    @Id
    @UuidGenerator
    @Column(name = "id_PK", updatable = false, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workoutId_FK", nullable = false)
    private Workout workout;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exerciseId_FK", nullable = false)
    private Exercise exercise;

    @Column(name = "setNumber", nullable = false)
    private Integer setNumber;

    @Column(name = "weightKg", precision = 5, scale = 2)
    private BigDecimal weightKg;

    @Column(name = "isPR")
    @Builder.Default
    private Boolean isPR = false;

    @Column(name = "reps")
    private Integer reps;

    @Column(name = "rpe")
    private Integer rpe;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private WorkoutSetType type;
}
