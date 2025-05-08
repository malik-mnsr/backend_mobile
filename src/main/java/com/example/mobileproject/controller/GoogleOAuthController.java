package com.example.mobileproject.controller;

import com.example.mobileproject.service.GoogleCalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class GoogleOAuthController {

    private final GoogleCalendarService gCal;

    /** Étape 1 : on génère l’URL d’autorisation à afficher au médecin */
    @GetMapping("/google/auth-url/{doctorId}")
    public ResponseEntity<String> buildAuthUrl(@PathVariable Long doctorId) {
        return ResponseEntity.ok(gCal.buildAuthUrl(doctorId));
    }

    /** Étape 2 : Google redirige ici avec ?code=...&state=doctorId */
    @GetMapping("/oauth2/callback")
    public ResponseEntity<String> oauthCallback(@RequestParam String code,
                                                @RequestParam String state) throws IOException {
        gCal.handleOAuthCallback(code, state);
        return ResponseEntity.ok("Compte Google Calendar connecté !");
    }
}
