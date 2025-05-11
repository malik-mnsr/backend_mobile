package com.example.mobileproject.dto;

public record DiagnosisSupportDTO(
        Integer supportId,
        Integer recordId,
        String  symptoms,
        String  possibleDiagnoses,
        String  recommendedTests,
        String  aiNotes,
        Float   confidenceScore
) {}
