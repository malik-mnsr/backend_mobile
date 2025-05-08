package com.example.mobileproject.repository;

import com.example.mobileproject.entity.Appointment;
import com.example.mobileproject.entity.Slot;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SlotRepository extends JpaRepository<Slot, Long> {

    /* Verrou pessimiste pour éviter la double‑réservation */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Slot s where s.id = :id")
    Optional<Slot> findByIdForUpdate(@Param("id") Long id);

    /* Créneaux libres d'un médecin à une date donnée */
    @Query("""
       select s from Slot s
       where s.doctor.id = :mid
         and s.reserved = false
         and date(s.startAt) = :date
    """)
    List<Slot> freeSlots(@Param("mid") Long doctorId,
                         @Param("date") LocalDate date);
}
