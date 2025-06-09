package com.example.mobileproject.controller;

import com.example.mobileproject.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/doctors/{doctorId}/fcm")
@RequiredArgsConstructor
public class DoctorNotificationController {

    private final DoctorService doctorService;

    /**
     * Enregistre le token FCM envoyé par l’app mobile du médecin.
     */
    @PostMapping("/token")
    public ResponseEntity<Void> setToken(
            @PathVariable Long doctorId,
            @RequestBody Map<String, String> body) {

        String token = body.get("token");
        doctorService.updateFcmToken(doctorId, token);
        return ResponseEntity.noContent().build();
    }
}
