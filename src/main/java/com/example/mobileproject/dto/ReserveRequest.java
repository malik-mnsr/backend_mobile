package com.example.mobileproject.dto;

/** Requête envoyée par le PATIENT pour réserver un créneau. */
public record ReserveRequest(
        Long patientId,
        String motif          // ou Motif enum si tu préfères
) {}
