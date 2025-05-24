package com.example.mobileproject.repository;

import com.example.mobileproject.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.print.Doc;
import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByEmail(String email);
    List<Doctor> findBySpecialty(String specialty);
   // Doctor findByEmail(String email);
   Optional<Doctor> findByEmailAndPhone(String email, String phone);
    boolean existsByEmail(String email);
    Doctor getDoctorById(Long id);
}
