package com.example.mobileproject.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
    @Column(columnDefinition = "LONGBLOB")  // MySQL-specific large binary storage
    private byte[] profilePicture;

    @Column(length = 50)
    private String profilePictureContentType;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private WorkingMode currentMode = WorkingMode.NORMAL;
    @Column(length = 1024)
    private String gAccessToken;

    @Column(length = 1024)
    private String gRefreshToken;

    private Long gTokenExpiryMs;   // epoch millis

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Patient> patients = new ArrayList<>();

}