package com.example.mobileproject.service;

import com.example.mobileproject.entity.Doctor;
import com.example.mobileproject.entity.Patient;
import com.example.mobileproject.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository patientRepository;
    private final DoctorService doctorService;

    // Create
    public Patient createPatient(Patient patient, Integer doctorId) {
        if (patientRepository.existsByEmail(patient.getEmail())) {
            throw new RuntimeException("Patient with this email already exists");
        }
        patient.setDoctor(doctorService.getDoctorById(doctorId));
        return patientRepository.save(patient);
    }

    // Read
    public Patient getPatientById(Integer id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
    }

    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    public List<Patient> getPatientsByDoctor(Integer doctorId) {
        return patientRepository.findByDoctorId(doctorId);
    }

    // Update
    public Patient updatePatient(Integer id, Patient updatedPatient) {
        Patient existing = getPatientById(id);
        existing.setFirstName(updatedPatient.getFirstName());
        existing.setLastName(updatedPatient.getLastName());
        existing.setPhone(updatedPatient.getPhone());
        existing.setAddress(updatedPatient.getAddress());
        return patientRepository.save(existing);
    }

    // Delete
    public void deletePatient(Integer id) {
        patientRepository.deleteById(id);
    }
    public void updateProfilePicture(Integer pateintId, MultipartFile file) throws IOException {
        Patient patient = patientRepository.findById(pateintId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        patient.setProfilePicture(file.getBytes());
        patient.setProfilePictureContentType(file.getContentType());
        patientRepository.save(patient);
    }

    public byte[] getProfilePicture(Integer pateintId) {
        return patientRepository.findById(pateintId)
                .map(Patient::getProfilePicture)
                .orElseThrow(() -> new RuntimeException("Patient or image not found"));
    }

    public String getContentType(Integer pateintId) {
        return patientRepository.findById(pateintId)
                .map(Patient::getProfilePictureContentType)
                .orElse("application/octet-stream");
    }
}

