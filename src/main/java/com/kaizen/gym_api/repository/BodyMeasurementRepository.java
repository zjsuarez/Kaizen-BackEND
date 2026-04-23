package com.kaizen.gym_api.repository;

import com.kaizen.gym_api.model.BodyMeasurement;
import com.kaizen.gym_api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BodyMeasurementRepository extends JpaRepository<BodyMeasurement, String> {

    List<BodyMeasurement> findByUserOrderByRecordedAtDesc(User user);

    Optional<BodyMeasurement> findByUserAndRecordedAt(User user, LocalDate date);

    // Statistics: Body Weight Trend - Lightweight projection grouped by date
    @Query("SELECT bm.recordedAt, AVG(bm.weightKg) FROM BodyMeasurement bm " +
            "WHERE bm.user.id = :userId AND bm.weightKg IS NOT NULL " +
            "GROUP BY bm.recordedAt " +
            "ORDER BY bm.recordedAt ASC")
    List<Object[]> findWeightTrendByUserId(@Param("userId") String userId);
}
