package com.example.mobileproject.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Un rendez-vous réservé par un patient sur un créneau proposé par un médecin.
 */
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Entity
@Table(
        name = "appointments",
        uniqueConstraints = @UniqueConstraint(
                name = "UK_appointment_slot",
                columnNames = "slot_id"
        )
)
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Créneau réservé – relation 1-1 obligatoire */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "slot_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_appointment_slot")
    )
    private Slot slot;

    /** Patient ayant réservé le rendez-vous */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "patient_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_appointment_patient")
    )
    private Patient patient;

    /** Motif sélectionné (CONSULTATION, URGENCE, …) */
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private Motif motif;

    /** État du rendez-vous (CONFIRMED, CANCELED, …) */
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private AppointmentStatus status = AppointmentStatus.CONFIRMED;

    @OneToOne(mappedBy = "appointment", cascade = CascadeType.ALL, orphanRemoval = true)
    private MedicalRecord medicalRecord;

    /** ID brut de l’événement Google Calendar (si besoin) */
    @Column(length = 128)
    private String googleEventId;

    /** URL HTML complète pour visualiser l’événement dans Google Calendar */
    @Column(name = "google_event_link", length = 512)
    private String googleEventLink;

    /** Dates de création / mise à jour (gérées par Hibernate) */
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    /* ───────────── Helpers pour exposer start/end via Slot ───────────── */

    /** Date + heure de début du rendez-vous (dérivée du Slot) */
    @Transient
    public LocalDateTime getStart() {
        return slot != null ? slot.getStartAt() : null;
    }

    /** Date + heure de fin du rendez-vous (dérivée du Slot) */
    @Transient
    public LocalDateTime getEnd() {
        return slot != null ? slot.getEndAt() : null;
    }
}
