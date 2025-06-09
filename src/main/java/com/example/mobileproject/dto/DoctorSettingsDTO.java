package com.example.mobileproject.dto;

public record DoctorSettingsDTO(
        Integer settingId,
        Long    doctorId,
        String  modePreferences,
        String  notificationPreferences,
        String  emergencyContacts,
        String  awayMessage
) {}
