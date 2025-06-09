package com.example.mobileproject.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity @Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Table(name = "doctor_settings")
public class DoctorSettings {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer settingId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", unique = true, nullable = false)
    private Doctor doctor;

    @Column(columnDefinition = "JSON", nullable = false)
    private String modePreferences;

    @Column(columnDefinition = "JSON", nullable = false)
    private String notificationPreferences;

    @Column(columnDefinition = "JSON")
    private String emergencyContacts;

    @Column(length = 500)
    private String awayMessage;
}
