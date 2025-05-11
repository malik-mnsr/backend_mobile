package com.example.mobileproject.dto;

import java.time.LocalDate;

public record MedicalRecordDTO(
        Integer   recordId,
        Long      patientId,
        Long      doctorId,
        Long      appointmentId,
        String    recordType,
        String    title,
        String    description,
        String    content,
        LocalDate dateCreated
) {}
