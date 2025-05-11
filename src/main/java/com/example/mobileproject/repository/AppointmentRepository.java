package com.example.mobileproject.repository;

import com.example.mobileproject.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository  // facultatif, mais explicite
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {


    List<Appointment> findAllBySlot_Doctor_Id(Long doctorId);

    List<Appointment> findAllByPatient_Id(Long patientId);

}
