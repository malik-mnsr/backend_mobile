package com.example.mobileproject.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Entity
@Table(name = "drug_reference")
public class DrugReference {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 120, nullable = false, unique = true)
    private String name;          // « Doliprane »

    @Column(length = 120)
    private String molecule;      // « Paracétamol »

    @Column(length = 10)
    private String atcCode;       // « N02BE01 »
}
