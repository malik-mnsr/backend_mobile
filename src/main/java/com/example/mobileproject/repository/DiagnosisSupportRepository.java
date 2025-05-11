package com.example.mobileproject.repository;

import com.example.mobileproject.entity.DiagnosisSupport;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface DiagnosisSupportRepository extends JpaRepository<DiagnosisSupport, Integer> {
    Optional<DiagnosisSupport> findByRecord_RecordId(Integer recordId);
}
