package com.example.mobileproject.controller;

import com.example.mobileproject.dto.DoctorDTO;
import com.example.mobileproject.entity.Doctor;
import com.example.mobileproject.service.DoctorService;
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
@RequestMapping("/api/doctors")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    @PostMapping
    public ResponseEntity<DoctorDTO> createDoctor(@RequestBody Doctor doctor) {
        Doctor saved = doctorService.createDoctor(doctor);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(doctorService.toDto(saved));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorDTO> getDoctor(@PathVariable Long id) {
        return ResponseEntity.ok(
                doctorService.toDto(
                        doctorService.getDoctorById(id)
                )
        );
    }

    @GetMapping
    public ResponseEntity<List<DoctorDTO>> getAllDoctors() {
        List<DoctorDTO> dtos = doctorService.getAllDoctors()
                .stream()
                .map(doctorService::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/specialty/{specialty}")
    public ResponseEntity<List<DoctorDTO>> getBySpecialty(@PathVariable String specialty) {
        List<DoctorDTO> dtos = doctorService.getDoctorsBySpecialty(specialty)
                .stream()
                .map(doctorService::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DoctorDTO> updateDoctor(
            @PathVariable Long id,
            @RequestBody Doctor doctor
    ) {
        Doctor updated = doctorService.updateDoctor(id, doctor);
        return ResponseEntity.ok(doctorService.toDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDoctor(@PathVariable Long id) {
        doctorService.deleteDoctor(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{id}/profile-picture",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadProfilePicture(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        doctorService.updateProfilePicture(id, file);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/profile-picture")
    public ResponseEntity<byte[]> getProfilePicture(@PathVariable Long id) {
        return ResponseEntity.ok()
                .contentType(
                        MediaType.parseMediaType(
                                doctorService.getContentType(id)
                        )
                )
                .body(doctorService.getProfilePicture(id));
    }

    @PostMapping("/login")
    public ResponseEntity<DoctorDTO> login(
            @RequestParam String email,
            @RequestParam String phone
    ) {
        Doctor doc = doctorService.getDoctorByEmailAndPhone(email, phone);
        return ResponseEntity.ok(doctorService.toDto(doc));
    }
}
