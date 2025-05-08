package com.example.mobileproject.dto;

/** Réponse renvoyée après réservation. */
public record AppointmentDTO(
        Long        id,
        SlotDTO     slot,
        PatientDTO  patient,
        String      motif,
        String      status,
        String  googleEventLink
) {}
