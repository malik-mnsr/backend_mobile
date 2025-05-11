package com.example.mobileproject.dto;

import java.time.LocalDate;

public record PrescriptionDTO(
        Integer          prescriptionId,
        Integer          recordId,
        String           medications,
        Integer          validityDays,
        String           status,
        boolean          sentToPharmacy,
        String           pharmacyDetails,
        LocalDate        dateIssued
) {}
