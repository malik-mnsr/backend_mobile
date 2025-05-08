package com.example.mobileproject.dto;

import java.time.LocalDateTime;

/** Requête envoyée par le MÉDECIN pour créer un créneau. */
public record SlotCreateDTO(
        LocalDateTime startAt,
        LocalDateTime endAt
) {}
