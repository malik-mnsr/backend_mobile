package com.example.mobileproject.service;

import com.example.mobileproject.dto.DoctorDTO;
import com.example.mobileproject.entity.Doctor;
import com.example.mobileproject.entity.WorkingMode;
import com.example.mobileproject.repository.DoctorRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;

    /* ---------------------- CREATE ---------------------- */
    public Doctor createDoctor(Doctor doctor) {
        if (doctorRepository.existsByEmail(doctor.getEmail())) {
            throw new IllegalStateException("Doctor with this email already exists");
        }
        return doctorRepository.save(doctor);
    }

    /* ---------------------- READ ------------------------ */
    public Doctor getDoctorById(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
    }

    public Doctor getDoctorByEmail(String email) {
        return doctorRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
    }

    public Doctor getDoctorByEmailAndPhone(String email, String phone) {
        return doctorRepository.findByEmailAndPhone(email, phone)
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));
    }

    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    public List<Doctor> getDoctorsBySpecialty(String specialty) {
        return doctorRepository.findBySpecialty(specialty);
    }

    /* ---------------------- UPDATE ---------------------- */
    public Doctor updateDoctor(Long id, Doctor updated) {
        Doctor existing = getDoctorById(id);
        existing.setFirstName(updated.getFirstName());
        existing.setLastName(updated.getLastName());
        existing.setSpecialty(updated.getSpecialty());
        existing.setPhone(updated.getPhone());
        return doctorRepository.save(existing);
    }

    /* ---------------------- DELETE ---------------------- */
    public void deleteDoctor(Long id) {
        doctorRepository.deleteById(id);
    }

    /* -------------------- PHOTO ------------------------- */
    public void updateProfilePicture(Long doctorId, MultipartFile file) throws IOException {
        Doctor doc = getDoctorById(doctorId);
        doc.setProfilePicture(file.getBytes());
        doc.setProfilePictureContentType(file.getContentType());
        doctorRepository.save(doc);
    }
    /**
     * Met à jour le deviceToken FCM du médecin.
     */
    public void updateFcmToken(Long doctorId, String token) {
        Doctor doc = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new EntityNotFoundException("Doctor not found: " + doctorId));
        doc.setFcmToken(token);
        doctorRepository.save(doc);
    }

    /**
     * Récupère le mode de travail actuel.
     */
    public WorkingMode getMode(Long doctorId) {
        return doctorRepository.findById(doctorId)
                .orElseThrow(() -> new EntityNotFoundException("Doctor not found: " + doctorId))
                .getCurrentMode();
    }

    /**
     * Met à jour le mode de travail du médecin.
     */
    public void updateMode(Long doctorId, WorkingMode newMode) {
        Doctor doc = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new EntityNotFoundException("Doctor not found: " + doctorId));
        doc.setCurrentMode(newMode);
        doctorRepository.save(doc);
    }
    public byte[] getProfilePicture(Long doctorId) {
        return getDoctorById(doctorId).getProfilePicture();
    }

    public String getContentType(Long doctorId) {
        return getDoctorById(doctorId).getProfilePictureContentType();
    }

    /* -------------------- MAPPING ----------------------- */
    public DoctorDTO toDto(Doctor d) {
        return new DoctorDTO(
                d.getId(),
                d.getFirstName(),
                d.getLastName(),
                d.getEmail(),
                d.getSpecialty(),
                d.getPhone()
        );

    }
}
