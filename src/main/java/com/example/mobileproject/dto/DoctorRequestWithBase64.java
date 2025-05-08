package com.example.mobileproject.dto;

import com.example.mobileproject.entity.WorkingMode;
import lombok.Data;

import java.util.List;

/** Requête JSON envoyée pour créer un médecin + image Base64 + patients */
@Data                // Lombok génère TOUS les getters / setters
public class DoctorRequestWithBase64 {

    private String firstName;
    private String lastName;
    private int age;
    private String email;
    private String specialty;
    private String phone;
    private WorkingMode currentMode;


    private String profilePictureBase64;

    private List<PatientRequest> patients;   // sous‑DTO défini plus bas
}
