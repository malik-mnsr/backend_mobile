// src/main/java/com/example/mobileproject/repository/MedicalRecordRepository.java
package com.example.mobileproject.repository;

import com.example.mobileproject.entity.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Integer> {

    // Avant : Optional<MedicalRecord> findByAppointment_AppointmentId(Long appointmentId);
    // Après : on navigue sur appointment.id
    Optional<MedicalRecord> findByAppointment_Id(Long appointmentId);

    List<MedicalRecord> findByPatient_Id(Long patientId);
}
