package com.example.mobileproject.dto;

public record DoctorDTO(
        Long   id,
        String firstName,
        String lastName,
        String email,
        String specialty,
        String phone
){}