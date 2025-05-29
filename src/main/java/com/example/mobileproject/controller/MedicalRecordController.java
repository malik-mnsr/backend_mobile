// src/main/java/com/example/mobileproject/controller/MedicalRecordController.java
package com.example.mobileproject.controller;

import com.example.mobileproject.dto.MedicalRecordDTO;
import com.example.mobileproject.entity.Motif;
import com.example.mobileproject.service.MedicalRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

/** Endpoints REST pour la gestion des dossiers médicaux (“records”) */
@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
public class MedicalRecordController {

    private final MedicalRecordService recordService;

    /* ----------------------------------------------------------------
     * 1. CRUD de base
     * ---------------------------------------------------------------- */

    /** Créer un dossier pour un rendez-vous existant */
    @PostMapping("/appointment/{appointmentId}")
    public ResponseEntity<MedicalRecordDTO> create(
            @PathVariable Long appointmentId,
            @RequestBody  MedicalRecordDTO body) {

        MedicalRecordDTO created = recordService.createForAppointment(appointmentId, body);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /** Récupérer le dossier lié à un rendez-vous */
    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<MedicalRecordDTO> byAppointment(@PathVariable Long appointmentId) {
        return ResponseEntity.ok(recordService.getByAppointment(appointmentId));
    }

    /** Mise à jour d’un dossier existant */
    @PutMapping("/{recordId}")
    public ResponseEntity<MedicalRecordDTO> update(
            @PathVariable Integer recordId,
            @RequestBody  MedicalRecordDTO body) {

        return ResponseEntity.ok(recordService.update(recordId, body));
    }

    /** Lecture simple d’un dossier par son id */
    @GetMapping("/{recordId}")
    public ResponseEntity<MedicalRecordDTO> byId(@PathVariable Integer recordId) {
        return ResponseEntity.ok(recordService.getById(recordId));
    }

    /** Suppression */
    @DeleteMapping("/{recordId}")
    public ResponseEntity<Void> delete(@PathVariable Integer recordId) {
        recordService.delete(recordId);
        return ResponseEntity.noContent().build();
    }

    /* ----------------------------------------------------------------
     * 2. Historique patient
     * ---------------------------------------------------------------- */

    /** Tous les dossiers d’un patient */
    @GetMapping("/patient/{patientId}/history")
    public ResponseEntity<List<MedicalRecordDTO>> patientHistory(@PathVariable Long patientId) {
        return ResponseEntity.ok(recordService.historyForPatient(patientId));
    }

    /* ----------------------------------------------------------------
     * 3. Filtres “intelligents” pour le médecin
     * ---------------------------------------------------------------- */

    /** Historique filtré (date et/ou motif) */
    @GetMapping("/doctor/{doctorId}/history")
    public List<MedicalRecordDTO> historyFiltered(
            @PathVariable Long doctorId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Motif motif) {

        return recordService.historyFiltered(doctorId, date, motif);
    }

    /** Historique du jour + motif précis */
    @GetMapping("/doctor/{doctorId}/history/today/{motif}")
    public List<MedicalRecordDTO> historyTodayMotif(
            @PathVariable Long doctorId,
            @PathVariable Motif motif) {

        return recordService.historyFiltered(doctorId, LocalDate.now(), motif);
    }

    /* ----------------------------------------------------------------
     * 4. Dossiers du jour (file courante)
     * ---------------------------------------------------------------- */


    @GetMapping("/doctor/{doctorId}/today")
    public List<MedicalRecordDTO> todayRecords(
            @PathVariable Long doctorId,
            @RequestParam(required = false) Motif motif) {

        return recordService.todayRecords(doctorId, motif);
    }

}
