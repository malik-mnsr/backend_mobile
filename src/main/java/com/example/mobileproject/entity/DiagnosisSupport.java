package com.example.mobileproject.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import static com.example.mobileproject.entity.JsonConverter.convertToJson;
import static com.example.mobileproject.entity.JsonConverter.parseJson;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "diagnosis_support")
public class DiagnosisSupport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer supportId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "record_id", unique = true, nullable = false)
    private MedicalRecord record;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String symptoms;

    @Column(columnDefinition = "JSON")
    private String possibleDiagnoses;  // Store as JSON string

    @Column(columnDefinition = "JSON")
    private String recommendedTests;  // Store as JSON string

    @Column(columnDefinition = "TEXT")
    private String aiNotes;

    @Column(precision = 5)  // e.g., 99.99
    private Float confidenceScore;

    // Optionally add JSON conversion methods
    public void setPossibleDiagnosesAsObject(Object obj) {
        this.possibleDiagnoses = convertToJson(obj);
    }

    public <T> T getPossibleDiagnosesAsObject(Class<T> type) {
        return parseJson(this.possibleDiagnoses, type);
    }
}