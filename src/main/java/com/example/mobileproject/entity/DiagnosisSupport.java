package com.example.mobileproject.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "diagnosis_support")
public class DiagnosisSupport {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer supportId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "record_id", nullable = false, unique = true)
    private MedicalRecord record;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String symptoms;

    @Column(columnDefinition = "JSON")
    private String possibleDiagnoses;

    @Column(columnDefinition = "JSON")
    private String recommendedTests;

    @Column(columnDefinition = "TEXT")
    private String aiNotes;

    @Column(precision = 5)
    private Float confidenceScore;
}
