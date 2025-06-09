package com.example.mobileproject.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "patients")
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // Changed to Integer (optional, but matches MySQL's INT)

    @Column(length = 100, unique = true)  // Email should be unique and reasonably sized
    private String email;

    @Column(length = 50)  // Added length constraints
    private String firstName;

    @Column(length = 50)
    private String lastName;

    private int age;

    @Column(length = 20)  // Phone numbers vary by country
    private String phone;

    @Column(length = 200)  // Addresses can be long
    private String address;

    @Lob
    @Column(columnDefinition = "LONGBLOB")  // MySQL-specific for large binary data
    private byte[] profilePicture;

    @Column(length = 50)  // e.g., "image/png", "image/jpeg"
    private String profilePictureContentType;

    // Patient.java
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    @JsonBackReference
    private Doctor doctor;

}