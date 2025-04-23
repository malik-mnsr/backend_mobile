package com.example.mobileproject.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "doctor_settings")
public class DoctorSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer settingId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", unique = true, nullable = false)
    private Doctor doctor;

    @Column(columnDefinition = "JSON", nullable = false)
    private String modePreferences;  // e.g., {"theme":"dark","defaultMode":"NORMAL"}

    @Column(columnDefinition = "JSON", nullable = false)
    private String notificationPreferences;  // e.g., {"email":true,"sms":false}

    @Column(columnDefinition = "JSON")
    private String emergencyContacts;  // e.g., [{"name":"John","phone":"+1234567890"}]

    @Column(length = 500)
    private String awayMessage;

    // Helper methods for JSON conversion
    public <T> T getModePreferences(Class<T> valueType) {
        return JsonUtils.parseJson(this.modePreferences, valueType);
    }

    public void setModePreferences(Object preferences) {
        this.modePreferences = JsonUtils.toJson(preferences);
    }
}