package com.example.mobileproject.service;

import com.example.mobileproject.entity.Doctor;
import com.example.mobileproject.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorService {
    private final DoctorRepository doctorRepository;

    // Create
    public Doctor createDoctor(Doctor doctor) {
        if (doctorRepository.existsByEmail(doctor.getEmail())) {
            throw new RuntimeException("Doctor with this email already exists");
        }
        return doctorRepository.save(doctor);
    }

    // Read
    public Doctor getDoctorById(Integer id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
    }

    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    public List<Doctor> getDoctorsBySpecialty(String specialty) {
        return doctorRepository.findBySpecialty(specialty);
    }

    // Update
    public Doctor updateDoctor(Integer id, Doctor updatedDoctor) {
        Doctor existing = getDoctorById(id);
        existing.setFirstName(updatedDoctor.getFirstName());
        existing.setLastName(updatedDoctor.getLastName());
        existing.setSpecialty(updatedDoctor.getSpecialty());
        existing.setPhone(updatedDoctor.getPhone());
        return doctorRepository.save(existing);
    }

    // Delete
    public void deleteDoctor(Integer id) {
        doctorRepository.deleteById(id);
    }
    public void updateProfilePicture(Integer doctorId, MultipartFile file) throws IOException {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        doctor.setProfilePicture(file.getBytes());
        doctor.setProfilePictureContentType(file.getContentType());
        doctorRepository.save(doctor);
    }

    public byte[] getProfilePicture(Integer doctorId) {
        return doctorRepository.findById(doctorId)
                .map(Doctor::getProfilePicture)
                .orElseThrow(() -> new RuntimeException("Doctor or image not found"));
    }

    public String getContentType(Integer doctorId) {
        return doctorRepository.findById(doctorId)
                .map(Doctor::getProfilePictureContentType)
                .orElse("application/octet-stream");
    }
}

