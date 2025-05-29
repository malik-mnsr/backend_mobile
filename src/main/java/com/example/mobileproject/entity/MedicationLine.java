package com.example.mobileproject.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Getter @Setter @NoArgsConstructor
public class MedicationLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;       // Doliprane
    private String dosage;     // 500 mg
    private String frequency;  // 3 × jour
    private int    durationDays;

    @ManyToOne(optional = false)
    private Prescription prescription;
}
