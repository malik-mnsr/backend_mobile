package com.example.mobileproject.controller;

import com.example.mobileproject.dto.AppointmentDTO;
import com.example.mobileproject.dto.ReserveRequest;
import com.example.mobileproject.entity.Appointment;
import com.example.mobileproject.entity.Motif;
import com.example.mobileproject.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    /** 1. Réserver un créneau */
    @PostMapping("/reserve/{slotId}")
    public ResponseEntity<AppointmentDTO> reserve(
            @PathVariable Long slotId,
            @RequestBody ReserveRequest req) throws IOException {

        Appointment appt = appointmentService.reserve(
                slotId,
                req.patientId(),
                Motif.valueOf(req.motif())
        );
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(appointmentService.toDto(appt));
    }

    /** 2. Annuler un RDV */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancel(@PathVariable Long id) throws IOException {
        appointmentService.cancel(id);
        return ResponseEntity.noContent().build();
    }

    /** 3a. Accepter une demande PENDING */
    @PostMapping("/{id}/accept")
    public ResponseEntity<AppointmentDTO> accept(@PathVariable Long id) throws IOException {
        Appointment appt = appointmentService.accept(id);
        return ResponseEntity.ok(appointmentService.toDto(appt));
    }

    /** 3b. Rejeter une demande PENDING */
    @PostMapping("/{id}/reject")
    public ResponseEntity<Void> reject(@PathVariable Long id) {
        appointmentService.reject(id);
        return ResponseEntity.noContent().build();
    }

    /** 4a. Liste des RDV d’un médecin */
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<AppointmentDTO>> listByDoctor(@PathVariable Long doctorId) {
        List<AppointmentDTO> dtos = appointmentService.findByDoctorId(doctorId)
                .stream()
                .map(appointmentService::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /** 4b. Liste des RDV d’un patient */
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<AppointmentDTO>> listByPatient(@PathVariable Long patientId) {
        List<AppointmentDTO> dtos = appointmentService.findByPatientId(patientId)
                .stream()
                .map(appointmentService::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // AppointmentController.java
    @GetMapping("/doctor/{doctorId}/queue/today")
    public List<AppointmentDTO> queueToday(@PathVariable Long doctorId) {
        return appointmentService.queueFiltered(doctorId,
                        LocalDate.now(), null)
                .stream().map(appointmentService::toDto).toList();
    }

    @GetMapping("/doctor/{doctorId}/queue/today/{motif}")
    public List<AppointmentDTO> queueTodayMotif(@PathVariable Long doctorId,
                                                @PathVariable Motif motif) {
        return appointmentService.queueFiltered(doctorId,
                        LocalDate.now(), motif)
                .stream().map(appointmentService::toDto).toList();
    }

    /* Variante libre : /queue/{yyyy-MM-dd}/{motif?} */
    @GetMapping("/doctor/{doctorId}/queue/{date}/{motif}")
    public List<AppointmentDTO> queueDateMotif(
            @PathVariable Long doctorId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @PathVariable Motif motif) {
        return appointmentService.queueFiltered(doctorId, date, motif)
                .stream().map(appointmentService::toDto).toList();
    }
}
