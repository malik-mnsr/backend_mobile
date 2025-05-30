package com.example.mobileproject.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter @NoArgsConstructor
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Ordonnance liée à un dossier médical existant */
    @ManyToOne(optional = false)
    private MedicalRecord medicalRecord;

    /** Notes générales (« Prendre pendant les repas », etc.) */
    private String note;

    /** Lignes de médicament : cascade + suppression orpheline */
    @OneToMany(mappedBy = "prescription",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<MedicationLine> medications = new ArrayList<>();

    /** Date de création, pour l’historique */
    private Instant dateCreated = Instant.now();
}
