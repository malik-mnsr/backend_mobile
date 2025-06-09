// src/main/java/com/example/mobileproject/repository/MedicalRecordRepository.java
package com.example.mobileproject.repository;

import com.example.mobileproject.entity.MedicalRecord;
import com.example.mobileproject.entity.Motif;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Integer> {

    /* ---------- requêtes simples ---------- */

    Optional<MedicalRecord> findByAppointment_Id(Long appointmentId);

    List<MedicalRecord> findByPatient_Id(Long patientId);

    // Pour filtrer par jour exact (un seul jour)
    @Query("""
               select r from MedicalRecord r
               where r.doctor.id = :docId
                 and (:date is null or function('date', r.dateCreated) = :date)
                 and (:motif is null or r.appointment.motif = :motif)
               order by r.dateCreated desc
            """)
    List<MedicalRecord> recordsFiltered(@Param("docId") Long docId,
                                        @Param("date") LocalDate date,
                                        @Param("motif") Motif motif);


    // Pour filtrer par intervalle (start-end)
    @Query("""
               select r from MedicalRecord r
               where r.doctor.id = :docId
                 and r.dateCreated >= :start
                 and r.dateCreated < :end
                 and (:motif is null or r.appointment.motif = :motif)
               order by r.dateCreated
            """)
    List<MedicalRecord> recordsForDay(@Param("docId") Long docId,
                                      @Param("start") LocalDateTime start,
                                      @Param("end") LocalDateTime end,
                                      @Param("motif") Motif motif);
}