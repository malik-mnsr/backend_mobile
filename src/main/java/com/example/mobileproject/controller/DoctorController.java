package com.example.mobileproject.controller;

import com.example.mobileproject.dto.DoctorDTO;
import com.example.mobileproject.dto.DoctorRequestWithBase64;
import com.example.mobileproject.entity.Doctor;
import com.example.mobileproject.entity.Patient;
import com.example.mobileproject.entity.WorkingMode;
import com.example.mobileproject.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/doctors")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class DoctorController {

    /** Service unique (fini les doublons) */
    private final DoctorService service;

    /* ─────────────────────────── CRUD de base ─────────────────────────── */

    @PostMapping
    public ResponseEntity<DoctorDTO> createDoctor(@RequestBody Doctor doctor) {
        Doctor saved = service.createDoctor(doctor);
        return ResponseEntity.status(HttpStatus.CREATED).body(service.toDto(saved));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorDTO> getDoctor(@PathVariable Long id) {
        return ResponseEntity.ok(service.toDto(service.getDoctorById(id)));
    }

    @GetMapping
    public ResponseEntity<List<DoctorDTO>> getAllDoctors() {
        List<DoctorDTO> dtos = service.getAllDoctors()
                .stream()
                .map(service::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/specialty/{specialty}")
    public ResponseEntity<List<Doctor>> getBySpecialty(@PathVariable String specialty) {
        return ResponseEntity.ok(service.getDoctorsBySpecialty(specialty));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DoctorDTO> updateDoctor(@PathVariable Long id,
                                                  @RequestBody Doctor doctor) {
        Doctor updated = service.updateDoctor(id, doctor);
        return ResponseEntity.ok(service.toDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDoctor(@PathVariable Long id) {
        service.deleteDoctor(id);
        return ResponseEntity.noContent().build();
    }

    /* ──────────────────────── Photo de profil ─────────────────────────── */

    @PostMapping(value = "/{id}/profile-picture",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadProfilePicture(@PathVariable Long id,
                                                     @RequestParam("file") MultipartFile file)
            throws IOException {
        service.updateProfilePicture(id, file);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/profile-picture")
    public ResponseEntity<byte[]> getProfilePicture(@PathVariable Long id) {
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(service.getContentType(id)))
                .body(service.getProfilePicture(id));
    }

    /* ─────────────────────────── Auth simplifiée ──────────────────────── */

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String email,
                                   @RequestParam String phone) {

        if (email.isBlank() || phone.isBlank()) {
            return ResponseEntity.badRequest().body("Email and phone are required.");
        }
        Doctor doctor = service.getDoctorByEmailAndPhone(email, phone);
        return ResponseEntity.ok(doctor);
    }

    /* ───────────────────── Création via Base64 (facultatif) ───────────── */

    @PostMapping(path = "/create-doctor/with-picture-base64",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Doctor> createDoctorWithPictureBase64(
            @RequestBody DoctorRequestWithBase64 req) throws IOException {

        Doctor doctor = new Doctor();
        doctor.setFirstName(req.getFirstName());
        doctor.setLastName(req.getLastName());
        doctor.setAge(req.getAge());
        doctor.setEmail(req.getEmail());
        doctor.setSpecialty(req.getSpecialty());
        doctor.setPhone(req.getPhone());
        doctor.setCurrentMode(req.getCurrentMode());
        doctor.setPatients(new ArrayList<>());

        /* Décodage de l’image Base64 */
        if (req.getProfilePictureBase64() != null && !req.getProfilePictureBase64().isBlank()) {
            String data  = req.getProfilePictureBase64().trim();
            String meta  = "image/jpeg";
            String base;

            if (data.contains(",")) {
                String[] parts = data.split(",", 2);
                meta = parts[0].split(":")[1].split(";")[0];   // ex: image/png
                base = parts[1];
            } else base = data;

            byte[] img = Base64.getDecoder().decode(base.replaceAll("\\s+", ""));
            doctor.setProfilePicture(img);
            doctor.setProfilePictureContentType(meta);
        }

        /* Patients éventuels inclus dans la requête */
        if (req.getPatients() != null) {
            List<Patient> patients = req.getPatients().stream().map(p -> {
                Patient pat = new Patient();
                pat.setFirstName(p.getFirstName());
                pat.setLastName(p.getLastName());
                pat.setAge(p.getAge());
                pat.setEmail(p.getEmail());
                pat.setPhone(p.getPhone());
                pat.setAddress(p.getAddress());
                pat.setDoctor(doctor);
                return pat;
            }).toList();
            doctor.setPatients(patients);
        }

        Doctor saved = service.createDoctor(doctor);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /* ───────────────────────── Modes intelligents ─────────────────────── */

    /** Lire le mode courant du médecin */
    @GetMapping("/{doctorId}/mode")
    public ResponseEntity<WorkingMode> getMode(@PathVariable Long doctorId) {
        return ResponseEntity.ok(service.getMode(doctorId));
    }

    /** Mettre à jour le mode courant */
    @PutMapping("/{doctorId}/mode")
    public ResponseEntity<Void> setMode(@PathVariable Long doctorId,
                                        @RequestBody String mode) {
        service.updateMode(doctorId, WorkingMode.valueOf(mode.toUpperCase()));
        return ResponseEntity.noContent().build();
    }
}
