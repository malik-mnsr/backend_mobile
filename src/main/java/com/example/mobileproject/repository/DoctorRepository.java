package com.example.mobileproject.repository;

import com.example.mobileproject.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Integer> {
    Optional<Doctor> findByEmail(String email);
    List<Doctor> findBySpecialty(String specialty);

    boolean existsByEmail(String email);
}