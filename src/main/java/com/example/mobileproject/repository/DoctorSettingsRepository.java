package com.example.mobileproject.repository;

import com.example.mobileproject.entity.DoctorSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface DoctorSettingsRepository extends JpaRepository<DoctorSettings, Integer> {
    Optional<DoctorSettings> findByDoctor_Id(Long doctorId);
}
