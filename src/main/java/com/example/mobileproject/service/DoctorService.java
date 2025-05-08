package com.example.mobileproject.service;

import com.example.mobileproject.entity.Doctor;
import com.example.mobileproject.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;

    /* ---------------------- CRUD ---------------------- */

    public Doctor createDoctor(Doctor doctor) {
        if (doctorRepository.existsByEmail(doctor.getEmail())) {
            throw new RuntimeException("Doctor with this email already exists");
        }
        return doctorRepository.save(doctor);
    }

    public Doctor getDoctorById(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
    }

    public Doctor getDoctorByEmail(String email) {
        return doctorRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
    }

    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    public List<Doctor> getDoctorsBySpecialty(String specialty) {
        return doctorRepository.findBySpecialty(specialty);
    }

    public Doctor updateDoctor(Long id, Doctor updated) {
        Doctor existing = getDoctorById(id);
        existing.setFirstName(updated.getFirstName());
        existing.setLastName(updated.getLastName());
        existing.setSpecialty(updated.getSpecialty());
        existing.setPhone(updated.getPhone());
        return doctorRepository.save(existing);
    }

    public void deleteDoctor(Long id) {
        doctorRepository.deleteById(id);
    }

    /* -------------------- PHOTO ----------------------- */

    public void updateProfilePicture(long doctorId, MultipartFile file) throws IOException {
        Doctor doc = getDoctorById(doctorId);
        doc.setProfilePicture(file.getBytes());
        doc.setProfilePictureContentType(file.getContentType());
        doctorRepository.save(doc);
    }

    public byte[] getProfilePicture(long doctorId) {
        return getDoctorById(doctorId).getProfilePicture();
    }

    public String getContentType(long doctorId) {
        return getDoctorById(doctorId).getProfilePictureContentType();
    }

    /* -------------------- LOGIN ----------------------- */

    public boolean authenticateDoctor(String email, String phone) {
        return doctorRepository.findByEmail(email)
                .map(d -> d.getPhone().equals(phone))
                .orElse(false);
    }

    public Doctor getDoctorByEmailAndPhone(String email, String phone) {
        return doctorRepository.findByEmailAndPhone(email, phone)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
    }
}
