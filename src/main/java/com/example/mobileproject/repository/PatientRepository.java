package com.example.mobileproject.repository;

import com.example.mobileproject.entity.Doctor;
import com.example.mobileproject.entity.Motif;
import com.example.mobileproject.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.print.Doc;
import java.util.List;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByEmail(String email);
    List<Patient> findByDoctorId(long doctorId);
    boolean existsByEmail(String email);
    @Query("""
        select distinct a.patient
        from   Appointment a
        where  a.motif = :motif
       """)
    List<Patient> findAllByMotif(@Param("motif") Motif motif);



}