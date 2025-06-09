package com.example.mobileproject.repository;

import com.example.mobileproject.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    /** Toutes les ordonnances d’un patient (via MedicalRecord → Patient) */
    List<Prescription> findByMedicalRecord_Patient_Id(Long patientId);
}
