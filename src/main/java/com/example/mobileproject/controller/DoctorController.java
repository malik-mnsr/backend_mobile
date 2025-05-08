package com.example.mobileproject.controller;


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

@CrossOrigin(origins = "http://192.168.224.33")  // Adjust as needed
@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {
    private final DoctorService doctorService;

    @PostMapping
    public ResponseEntity<Doctor> createDoctor(@RequestBody Doctor doctor) {
        return ResponseEntity.status(HttpStatus.CREATED).body(doctorService.createDoctor(doctor));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Doctor> getDoctor(@PathVariable long id) {
        return ResponseEntity.ok(doctorService.getDoctorById(id));
    }

    @GetMapping
    public ResponseEntity<List<Doctor>> getAllDoctors() {
        return ResponseEntity.ok(doctorService.getAllDoctors());
    }

    @GetMapping("/specialty/{specialty}")
    public ResponseEntity<List<Doctor>> getBySpecialty(@PathVariable String specialty) {
        return ResponseEntity.ok(doctorService.getDoctorsBySpecialty(specialty));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Doctor> updateDoctor(@PathVariable long id, @RequestBody Doctor doctor) {
        return ResponseEntity.ok(doctorService.updateDoctor(id, doctor));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDoctor(@PathVariable long id) {
        doctorService.deleteDoctor(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{id}/profile-picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadProfilePicture(
            @PathVariable long id,
            @RequestParam("file") MultipartFile file) throws IOException {
        doctorService.updateProfilePicture(id, file);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/profile-picture")
    public ResponseEntity<byte[]> getProfilePicture(@PathVariable long id) {
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(doctorService.getContentType(id)))
                .body(doctorService.getProfilePicture(id));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam(required = true) String email, @RequestParam(required = true) String phone) {
        if (email.isEmpty() || phone.isEmpty()) {
            return ResponseEntity.badRequest().body("Email and phone are required.");
        }

        // Authenticate doctor using email and phone
        Doctor doctor = doctorService.getDoctorByEmailAndPhone(email, phone);

        if (doctor != null) {
            return ResponseEntity.ok(doctor); // Return the Doctor object if authentication is successful
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }

    @PostMapping(path = "/create-doctor/with-picture-base64", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Doctor> createDoctorWithPictureBase64(
            @RequestBody DoctorRequestWithBase64 request) throws IOException {  // New DTO with Base64 field

        Doctor doctor = new Doctor();
        doctor.setFirstName(request.getFirstName());
        doctor.setLastName(request.getLastName());
        doctor.setAge(request.getAge());
        doctor.setEmail(request.getEmail());
        doctor.setSpecialty(request.getSpecialty());
        doctor.setPhone(request.getPhone());
        doctor.setCurrentMode(request.getCurrentMode());
        doctor.setPatients(new ArrayList<>());

        // Handle Base64 image if present
        String base64Data = request.getProfilePictureBase64().trim();
        String contentType = "image/jpeg"; // valeur par défaut, si le préfixe est absent
        String base64Image;

        if (base64Data.contains(",")) {
            // Chaîne avec préfixe type: "data:image/jpeg;base64,..."
            String[] parts = base64Data.split(",", 2);
            String meta = parts[0]; // e.g., "data:image/jpeg;base64"
            base64Image = parts[1];
            if (meta.contains(":") && meta.contains(";")) {
                contentType = meta.split(":")[1].split(";")[0]; // Extrait "image/jpeg"
            }
        } else {
            // Chaîne brute (pas de préfixe)
            base64Image = base64Data;
        }

        base64Image = base64Image.replaceAll("\\s+", ""); // Nettoyer les espaces et sauts de ligne
        byte[] imageBytes = Base64.getDecoder().decode(base64Image);
        doctor.setProfilePicture(imageBytes);
        doctor.setProfilePictureContentType(contentType);

        // Handle patients (same as before)
        if (request.getPatients() != null && !request.getPatients().isEmpty()) {
            List<Patient> patients = request.getPatients().stream().map(p -> {
                Patient patient = new Patient();
                patient.setFirstName(p.getFirstName());
                patient.setLastName(p.getLastName());
                patient.setAge(p.getAge());
                patient.setEmail(p.getEmail());
                patient.setPhone(p.getPhone());
                patient.setAddress(p.getAddress());
                patient.setDoctor(doctor);
                return patient;
            }).collect(Collectors.toList());
            doctor.setPatients(patients);
        }

        Doctor savedDoctor = doctorService.createDoctor(doctor);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDoctor);
    }
}
