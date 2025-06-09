package com.example.mobileproject.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Entity
@Table(name = "slots")
public class Slot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* Médecin qui propose le créneau */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_slot_doctor"))
    private Doctor doctor;

    /* Début / fin du créneau */
    private LocalDateTime startAt;
    private LocalDateTime endAt;

    /* Réservé ou non */
    private boolean reserved = false;

    /* Rendez‑vous associé (s’il est réservé) */
    @OneToOne(mappedBy = "slot")
    private Appointment appointment;
}
