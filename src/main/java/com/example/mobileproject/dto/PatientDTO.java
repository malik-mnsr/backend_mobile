package com.example.mobileproject.dto;

public record PatientDTO(
        Long   id,
        String firstName,
        String lastName,
        int    age,
        String email,
        String phone,
        String address
) {}
