package com.example.mobileproject.repository;

import com.example.mobileproject.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findAllBySlot_Doctor_Id(Long doctorId);
    List<Appointment> findAllByPatient_Id(Long patientId);
}
