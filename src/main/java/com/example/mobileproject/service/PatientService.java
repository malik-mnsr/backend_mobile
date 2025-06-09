package com.example.mobileproject.service;

import com.example.mobileproject.dto.PatientDTO;
import com.example.mobileproject.entity.Doctor;
import com.example.mobileproject.entity.Patient;
import com.example.mobileproject.repository.DoctorRepository;
import com.example.mobileproject.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepo;
    private final DoctorRepository doctorService;

    /* ─────────── CREATE ─────────── */
    public Patient createPatient(Patient p, Long doctorId) {
        if (patientRepo.existsByEmail(p.getEmail())) {
            throw new IllegalStateException("Email already used");
        }
        p.setDoctor(doctorService.getDoctorById(doctorId));
        return patientRepo.save(p);
    }

    /* ─────────── READ ─────────── */
    public Patient getPatientById(Long id) {
        return patientRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
    }

    public List<Patient> getAllPatients() {
        return patientRepo.findAll();
    }

    public List<Patient> getPatientsByDoctor(Long doctorId) {
        return patientRepo.findByDoctorId(doctorId);
    }

    /* ─────────── UPDATE ─────────── */
    public Patient updatePatient(Long id, Patient upd) {
        Patient ex = getPatientById(id);
        ex.setFirstName(upd.getFirstName());
        ex.setLastName(upd.getLastName());
        ex.setPhone(upd.getPhone());
        ex.setAddress(upd.getAddress());
        return patientRepo.save(ex);
    }

    /* ─────────── DELETE ─────────── */
    public void deletePatient(Long id) {
        patientRepo.deleteById(id);
    }

    /* ─────────── PHOTO DE PROFIL ─────────── */
    public void updateProfilePicture(Long patientId, MultipartFile file) throws IOException {
        Patient p = getPatientById(patientId);
        p.setProfilePicture(file.getBytes());
        p.setProfilePictureContentType(file.getContentType());
        patientRepo.save(p);
    }

    public byte[] getProfilePicture(Long patientId) {
        return getPatientById(patientId).getProfilePicture();
    }

    public String getContentType(Long patientId) {
        return getPatientById(patientId)
                .getProfilePictureContentType();
    }

    public PatientDTO toDto(Patient p) {
        return new PatientDTO(
                p.getId(),
                p.getFirstName(),
                p.getLastName(),
                p.getAge(),
                p.getEmail(),
                p.getPhone(),
                p.getAddress()
        );
    }

    }
