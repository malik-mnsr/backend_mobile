package com.example.mobileproject.controller;

import com.example.mobileproject.dto.AppointmentDTO;
import com.example.mobileproject.dto.ReserveRequest;
import com.example.mobileproject.dto.SlotDTO;
import com.example.mobileproject.dto.PatientDTO;
import com.example.mobileproject.entity.Appointment;
import com.example.mobileproject.entity.Motif;
import com.example.mobileproject.service.AppointmentService;
import com.example.mobileproject.service.PatientService;
import com.example.mobileproject.service.SlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final SlotService slotService;      // mapping Slot → SlotDTO
    private final PatientService patientService;   // mapping Patient → PatientDTO

    /* ─────────── RÉSERVATION ─────────── */
    @PostMapping("/reserve/{slotId}")
    public ResponseEntity<AppointmentDTO> reserve(@PathVariable Long slotId,
                                                  @RequestBody ReserveRequest req) throws IOException {

        Appointment appt = appointmentService.reserve(
                slotId,
                req.patientId(),
                Motif.valueOf(req.motif())
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toDto(appt));
    }

    /* ─────────── ANNULATION ─────────── */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancel(@PathVariable Long id) throws IOException {
        appointmentService.cancel(id);
        return ResponseEntity.noContent().build();
    }

    /* ─────────── LISTE MÉDECIN ─────────── */
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<AppointmentDTO>> listByDoctor(@PathVariable Long doctorId) {
        return ResponseEntity.ok(
                appointmentService.findByDoctorId(doctorId).stream()
                        .map(this::toDto)
                        .collect(Collectors.toList()));
    }

    /* ─────────── LISTE PATIENT ─────────── */
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<AppointmentDTO>> listByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(
                appointmentService.findByPatientId(patientId).stream()
                        .map(this::toDto)
                        .collect(Collectors.toList()));
    }

    /* ─────────── Helper mapping Appointment → DTO ─────────── */
    private AppointmentDTO toDto(Appointment a) {
        String link = a.getGoogleEventLink();  // c’est déjà l’URL complète
        return new AppointmentDTO(
                a.getId(),
                slotService.toDto(a.getSlot()),
                patientService.toDto(a.getPatient()),
                a.getMotif().name(),
                a.getStatus().name(),
                a.getGoogleEventLink()
        );


    }
}