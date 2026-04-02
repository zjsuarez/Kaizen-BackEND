package com.kaizen.gym_api.repository;

import com.kaizen.gym_api.model.BodyMeasurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BodyMeasurementRepository extends JpaRepository<BodyMeasurement, String> {

    List<BodyMeasurement> findByUser_EmailOrderByDateDesc(String email);

    Optional<BodyMeasurement> findFirstByUser_EmailOrderByDateDesc(String email);
}
