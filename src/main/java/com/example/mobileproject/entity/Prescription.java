package com.example.mobileproject.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "prescriptions")
public class Prescription {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer prescriptionId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "record_id", nullable = false, unique = true)
    private MedicalRecord record;

    @Column(columnDefinition = "JSON", nullable = false)
    private String medications;  // JSON list of {name,dosage,frequency}

    @Column(nullable = false)
    private Integer validityDays = 30;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private PrescriptionStatus status = PrescriptionStatus.ACTIVE;

    @Column(nullable = false)
    private boolean sentToPharmacy = false;

    @Column(length = 200)
    private String pharmacyDetails;

    @Column(nullable = false, updatable = false)
    private LocalDate dateIssued = LocalDate.now();
}
