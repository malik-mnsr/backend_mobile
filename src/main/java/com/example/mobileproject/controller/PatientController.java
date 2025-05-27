package com.example.mobileproject.controller;

import com.example.mobileproject.dto.PatientDTO;
import com.example.mobileproject.dto.PatientRequestWithBase64;
import com.example.mobileproject.entity.Doctor;
import com.example.mobileproject.entity.Motif;
import com.example.mobileproject.entity.Patient;
import com.example.mobileproject.service.PatientService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Base64;
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
    public ResponseEntity<List<PatientDTO>> getPatientsByDoctor(@PathVariable Long doctorId) {
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
    @GetMapping("/motif/{motif}")
    public ResponseEntity<List<PatientDTO>> getPatientsByMotif(@PathVariable String motif) {
        try {
            Motif m = Motif.valueOf(motif.toUpperCase());
            List<PatientDTO> dtos = patientService.getPatientsByMotif(m);
            return ResponseEntity.ok(dtos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
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
    @PostMapping(path = "/create-patient/with-picture-base64", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public ResponseEntity<Patient> createPatientWithPictureBase64(
            @RequestBody PatientRequestWithBase64 request) throws IOException {

        // Create new patient entity
        Patient patient = new Patient();
        patient.setFirstName(request.getFirstName());
        patient.setLastName(request.getLastName());
        patient.setAge(request.getAge());
        patient.setEmail(request.getEmail());
        patient.setPhone(request.getPhone());
        patient.setAddress(request.getAddress());
        patient.setDoctor(request.getDoctorId());
        System.out.println(request.getDoctorId() + " " + request.getFirstName() + " " + request.getLastName());
        if (request.getProfilePictureBase64() != null && !request.getProfilePictureBase64().isEmpty()) {
            try {
                String base64Data = request.getProfilePictureBase64().trim();
                String contentType = "image/jpeg"; // default value
                String base64Image;

                if (base64Data.contains(",")) {
                    // Handle data URI scheme
                    String[] parts = base64Data.split(",", 2);
                    String meta = parts[0];
                    base64Image = parts[1];
                    if (meta.contains(":") && meta.contains(";")) {
                        contentType = meta.split(":")[1].split(";")[0];
                    }
                } else {
                    // Handle raw Base64 string
                    base64Image = base64Data;
                }

                base64Image = base64Image.replaceAll("\\s+", "");
                byte[] imageBytes = Base64.getDecoder().decode(base64Image);
                patient.setProfilePicture(imageBytes);
                patient.setProfilePictureContentType(contentType);
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid image data");
            }
        }
        Patient savedPatient;

                savedPatient = patientService.createPatient(patient, patient.getDoctor().getId());


        return ResponseEntity.status(HttpStatus.CREATED).body(savedPatient);
    }
}