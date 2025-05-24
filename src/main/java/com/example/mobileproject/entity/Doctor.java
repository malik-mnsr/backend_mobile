package com.example.mobileproject.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Entity
@Table(name = "doctors")
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50)
    private String firstName;

    @Column(length = 50)
    private String lastName;

    private int age;

    @Column(length = 100, unique = true)
    private String email;

    @Column(length = 100)
    private String specialty;

    @Column(length = 20)
    private String phone;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] profilePicture;

    @Column(length = 50)
    private String profilePictureContentType;

    /** Mode de travail courant (enum WorkingMode) */
    @Enumerated(EnumType.STRING)
    @Column(name = "current_mode", length = 20)
    private WorkingMode currentMode = WorkingMode.NORMAL;

    /** OAuth2 tokens pour Google Calendar */
    @Column(length = 1024)
    private String gAccessToken;

    @Column(length = 1024)
    private String gRefreshToken;

    @Column
    private Long gTokenExpiryMs;

    /** Token FCM du device mobile du médecin */
    @Column(name = "fcm_token", length = 512)
    private String fcmToken;
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference  // <- CORRECT: parent side
    private List<Patient> patients = new ArrayList<>();
}
