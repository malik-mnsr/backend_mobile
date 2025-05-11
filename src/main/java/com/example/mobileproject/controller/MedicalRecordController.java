package com.example.mobileproject.controller;

import com.example.mobileproject.dto.MedicalRecordDTO;
import com.example.mobileproject.service.MedicalRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
public class MedicalRecordController {

    private final MedicalRecordService recordService;

    /** Créer un dossier pour un rdv existant */
    @PostMapping("/appointment/{appointmentId}")
    public ResponseEntity<MedicalRecordDTO> create(
            @PathVariable Long appointmentId,
            @RequestBody MedicalRecordDTO dto) {
        MedicalRecordDTO created = recordService
                .createForAppointment(appointmentId, dto);
        return ResponseEntity.status(201).body(created);
    }

    /** Récupérer le dossier associé à un rdv */
    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<MedicalRecordDTO> getByAppointment(
            @PathVariable Long appointmentId) {
        return ResponseEntity.ok(
                recordService.getByAppointment(appointmentId));
    }

    /** Mettre à jour un dossier existant */
    @PutMapping("/{recordId}")
    public ResponseEntity<MedicalRecordDTO> update(
            @PathVariable Integer recordId,
            @RequestBody MedicalRecordDTO dto) {
        return ResponseEntity.ok(
                recordService.update(recordId, dto));
    }

    /** Historique des dossiers d’un patient */
    @GetMapping("/patient/{patientId}/history")
    public ResponseEntity<List<MedicalRecordDTO>> historyForPatient(
            @PathVariable Long patientId) {
        return ResponseEntity.ok(
                recordService.historyForPatient(patientId));
    }
    @GetMapping("/{recordId}")
    public ResponseEntity<MedicalRecordDTO> getById(
            @PathVariable Integer recordId) {
        return ResponseEntity.ok(recordService.getById(recordId));
    }

    /** Supprimer un dossier */
    @DeleteMapping("/{recordId}")
    public ResponseEntity<Void> delete(
            @PathVariable Integer recordId) {
        recordService.delete(recordId);
        return ResponseEntity.noContent().build();
    }
}
