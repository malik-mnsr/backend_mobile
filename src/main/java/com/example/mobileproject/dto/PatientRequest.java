package com.example.mobileproject.dto;

import lombok.Data;

@Data
public class PatientRequest {
    private String firstName;
    private String lastName;
    private Integer age;
    private String email;
    private String phone;
    private String address;
}
