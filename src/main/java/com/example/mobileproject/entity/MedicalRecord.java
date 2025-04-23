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
@Table(name = "medical_records")
public class MedicalRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer recordId;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @OneToOne
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    @Enumerated(EnumType.STRING)
    private RecordType recordType;

    private String title;
    private String description;

    @Column(columnDefinition = "JSON")
    private String content;

    private LocalDate dateCreated;

    @OneToOne(mappedBy = "record")
    private Prescription prescription;

    @OneToOne(mappedBy = "record")
    private DiagnosisSupport diagnosisSupport;


}

