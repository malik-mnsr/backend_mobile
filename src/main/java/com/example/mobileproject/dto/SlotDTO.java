package com.example.mobileproject.dto;

import java.time.LocalDateTime;

/** Réponse renvoyée au front (patient ou médecin). */
public record SlotDTO(
        Long id,
        LocalDateTime startAt,
        LocalDateTime endAt,
        boolean reserved
) {}
