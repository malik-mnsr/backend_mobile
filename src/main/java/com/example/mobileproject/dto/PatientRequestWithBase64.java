package com.example.mobileproject.dto;


import com.example.mobileproject.entity.Doctor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PatientRequestWithBase64 {
    // Getters and Setters
    private Long id;
    private String firstName;
    private String lastName;
    private int age;
    private String email;
    private String phone;
    private String address;
    private String profilePictureBase64;
    private Doctor doctorId;
    // Constructors
    public PatientRequestWithBase64() {
    }

    public PatientRequestWithBase64(Long id, String firstName, String lastName, int age,
                      String email, String phone, String address, String profilePictureBase64, Doctor doctorId) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.profilePictureBase64 = profilePictureBase64;
        this.doctorId = doctorId;
    }

    // Optional: toString() method
    @Override
    public String toString() {
        return "PatientDTO{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", age=" + age +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", profilePictureBase64='" + (profilePictureBase64 != null ? "[BASE64_IMAGE]" : "null") + '\'' +
                '}';
    }
}