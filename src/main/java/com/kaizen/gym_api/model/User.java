package com.kaizen.gym_api.model;

import com.kaizen.gym_api.model.enums.EquipmentType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "Users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @UuidGenerator
    @Column(name = "id_PK", updatable = false, nullable = false)
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

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "equipmentAvailable", columnDefinition = "JSON")
    private List<EquipmentType> equipmentAvailable;

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
