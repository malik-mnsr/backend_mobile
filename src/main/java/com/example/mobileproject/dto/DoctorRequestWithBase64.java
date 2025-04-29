package com.example.mobileproject.dto;

import com.example.mobileproject.entity.WorkingMode;
import lombok.Data;

import java.util.List;

@Data
public class DoctorRequestWithBase64 {
    private String firstName;
    private String lastName;
    private int age;
    private String email;
    private String specialty;
    private String phone;
    private WorkingMode currentMode;
    private String profilePictureBase64;  // Base64-encoded image string
    private List<PatientRequest> patients;

    // Getters and setters
}

