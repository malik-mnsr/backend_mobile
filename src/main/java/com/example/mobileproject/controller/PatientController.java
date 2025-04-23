package com.example.mobileproject.controller;

import com.example.mobileproject.entity.Doctor;
import com.example.mobileproject.entity.Patient;
import com.example.mobileproject.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {
    private final PatientService patientService;

    @PostMapping("/doctor/{doctorId}")
    public ResponseEntity<Patient> createPatient(
            @PathVariable Integer doctorId,
            @RequestBody Patient patient) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(patientService.createPatient(patient, doctorId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Patient> getPatient(@PathVariable Integer id) {
        return ResponseEntity.ok(patientService.getPatientById(id));
    }

    @GetMapping
    public ResponseEntity<List<Patient>> getAllPatients() {
        return ResponseEntity.ok(patientService.getAllPatients());
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<Patient>> getByDoctor(@PathVariable Integer doctorId) {
        return ResponseEntity.ok(patientService.getPatientsByDoctor(doctorId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Patient> updatePatient(
            @PathVariable Integer id,
            @RequestBody Patient patient) {
        return ResponseEntity.ok(patientService.updatePatient(id, patient));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Integer id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }
    @PostMapping(value = "/{id}/profile-picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadProfilePicture(
            @PathVariable Integer id,
            @RequestParam("file") MultipartFile file) throws IOException {
        patientService.updateProfilePicture(id, file);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/profile-picture")
    public ResponseEntity<byte[]> getProfilePicture(@PathVariable Integer id) {
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(patientService.getContentType(id)))
                .body(patientService.getProfilePicture(id));
    }
}
