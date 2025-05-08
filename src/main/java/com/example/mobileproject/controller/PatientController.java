package com.example.mobileproject.controller;

import com.example.mobileproject.dto.PatientDTO;
import com.example.mobileproject.entity.Patient;
import com.example.mobileproject.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    /* ─────────── CREATE ─────────── */
    @PostMapping("/doctor/{doctorId}")
    public ResponseEntity<PatientDTO> createPatient(@PathVariable Long doctorId,
                                                    @RequestBody Patient body) {
        Patient saved = patientService.createPatient(body, doctorId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(patientService.toDto(saved));
    }

    /* ─────────── READ ─────────── */
    @GetMapping("/{id}")
    public ResponseEntity<PatientDTO> getPatient(@PathVariable Long id) {
        return ResponseEntity.ok(
                patientService.toDto(patientService.getPatientById(id)));
    }

    @GetMapping
    public ResponseEntity<List<PatientDTO>> getAllPatients() {
        return ResponseEntity.ok(
                patientService.getAllPatients().stream()
                        .map(patientService::toDto)
                        .collect(Collectors.toList()));
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<PatientDTO>> getByDoctor(@PathVariable Long doctorId) {
        return ResponseEntity.ok(
                patientService.getPatientsByDoctor(doctorId).stream()
                        .map(patientService::toDto)
                        .collect(Collectors.toList()));
    }

    /* ─────────── UPDATE ─────────── */
    @PutMapping("/{id}")
    public ResponseEntity<PatientDTO> updatePatient(@PathVariable Long id,
                                                    @RequestBody Patient body) {
        return ResponseEntity.ok(
                patientService.toDto(patientService.updatePatient(id, body)));
    }

    /* ─────────── DELETE ─────────── */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }

    /* ─────────── PHOTO DE PROFIL ─────────── */
    @PostMapping(value = "/{id}/profile-picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadProfile(@PathVariable Long id,
                                              @RequestParam("file") MultipartFile file) throws IOException {
        patientService.updateProfilePicture(id, file);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/profile-picture")
    public ResponseEntity<byte[]> downloadProfile(@PathVariable Long id) {
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(patientService.getContentType(id)))
                .body(patientService.getProfilePicture(id));
    }
}
