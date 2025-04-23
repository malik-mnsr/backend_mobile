package com.example.mobileproject.entity;

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
@Table(name = "doctors")
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

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
}