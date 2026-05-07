package com.kaizen.gym_api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
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
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Routine routine;

    @Column(name = "orderIndex", nullable = false)
    private Integer orderIndex;

    @Column(name = "targetSets")
    private Integer targetSets;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customExerciseId_FK")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Exercise customExercise;

    @Column(name = "builtinExerciseKey")
    private String builtinExerciseKey;
}
