package com.example.mobileproject.repository;

import com.example.mobileproject.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PrescriptionRepository extends JpaRepository<Prescription, Integer> {
    Optional<Prescription> findByRecord_RecordId(Integer recordId);
}
