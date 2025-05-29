package com.example.mobileproject.repository;

import com.example.mobileproject.entity.Appointment;
import com.example.mobileproject.entity.Motif;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository  // facultatif, mais explicite
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {


    List<Appointment> findAllBySlot_Doctor_Id(Long doctorId);

    List<Appointment> findAllByPatient_Id(Long patientId);

    @Query("""
               select a from Appointment a
               where a.slot.doctor.id = :docId
                 and date(a.slot.startAt) = current_date
                 and a.status = 'CONFIRMED'
               order by a.slot.startAt
            """)
    List<Appointment> queueForToday(@Param("docId") Long docId);

    @Query("""
   select a from Appointment a
   where a.slot.doctor.id = :docId
     and (:date is null  or date(a.slot.startAt) = :date)
     and (:motif is null or a.motif          = :motif)
     and a.status = 'CONFIRMED'
   order by a.slot.startAt
""")
    List<Appointment> queueFiltered(@Param("docId") Long docId,
                                    @Param("date") LocalDate date,
                                    @Param("motif") Motif motif);



}
