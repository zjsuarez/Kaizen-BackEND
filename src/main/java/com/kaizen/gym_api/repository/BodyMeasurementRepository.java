package com.kaizen.gym_api.repository;

import com.kaizen.gym_api.model.BodyMeasurement;
import com.kaizen.gym_api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BodyMeasurementRepository extends JpaRepository<BodyMeasurement, Long> {

    List<BodyMeasurement> findByUserOrderByRecordedAtDesc(User user);

    Optional<BodyMeasurement> findByUserAndRecordedAt(User user, LocalDate date);
}
