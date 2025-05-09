package com.example.mobileproject.repository;

import com.example.mobileproject.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Integer> {
    Optional<Patient> findByEmail(String email);
    List<Patient> findByDoctorId(Integer doctorId);
    boolean existsByEmail(String email);

}