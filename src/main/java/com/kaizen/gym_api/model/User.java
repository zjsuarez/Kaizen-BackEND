package com.kaizen.gym_api.model;

import com.kaizen.gym_api.model.enums.EquipmentType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.sql.Timestamp;
import java.util.Set;

@Entity
@Table(name = "Users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @UuidGenerator
    @Column(name = "id_PK", updatable = false, nullable = false, columnDefinition = "CHAR(36)")
    private String id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "passwordHash", nullable = false)
    private String passwordHash;

    @Column(name = "profilePic")
    private String profilePic;

    @Column(name = "primaryGoal")
    private String primaryGoal;

    @ElementCollection(targetClass = EquipmentType.class, fetch = FetchType.LAZY)
    @CollectionTable(
            name = "UserEquipment",
            joinColumns = @JoinColumn(name = "userId_FK", columnDefinition = "CHAR(36)")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "equipmentType")
    private Set<EquipmentType> equipmentAvailable;

    @Column(name = "unitSystem", length = 50)
    private String unitSystem;

    @Column(name = "restTimerDefault")
    private Integer restTimerDefault;

    @CreationTimestamp
    @Column(name = "createdAt", updatable = false, nullable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "updatedAt", nullable = false)
    private Timestamp updatedAt;
}
