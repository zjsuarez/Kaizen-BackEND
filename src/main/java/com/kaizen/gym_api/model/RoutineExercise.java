package com.kaizen.gym_api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "RoutineExercises")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoutineExercise {

    @Id
    @UuidGenerator
    @Column(name = "id_PK", updatable = false, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "routineId_FK", nullable = false)
    private Routine routine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exerciseId_FK", nullable = false)
    private Exercise exercise;

    @Column(name = "orderIndex", nullable = false)
    private Integer orderIndex;

    @Column(name = "targetSets")
    private Integer targetSets;

    @Column(name = "targetReps")
    private Integer targetReps;

    @Column(name = "restSeconds")
    private Integer restSeconds;
}
