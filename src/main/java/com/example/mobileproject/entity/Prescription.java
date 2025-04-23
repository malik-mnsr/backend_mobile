package com.example.mobileproject.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "prescriptions")
public class Prescription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer prescriptionId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "record_id", nullable = false, unique = true)
    private MedicalRecord record;

    @Column(columnDefinition = "JSON", nullable = false)
    private String medications;  // e.g., [{"name":"Amoxicillin","dosage":"500mg","frequency":"2x daily"}]

    @Column(nullable = false)
    private Integer validityDays = 30;  // Default 30-day validity

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private PrescriptionStatus status = PrescriptionStatus.ACTIVE;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean sentToPharmacy;

    @Column(length = 200)
    private String pharmacyDetails;

    @Column(nullable = false, updatable = false)
    private LocalDate dateIssued = LocalDate.now();



}