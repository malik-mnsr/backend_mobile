package com.example.mobileproject.service;

import com.example.mobileproject.entity.Appointment;
import com.example.mobileproject.entity.Doctor;
import com.example.mobileproject.repository.DoctorRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.ZoneOffset;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GoogleCalendarService {

    @Value("${google.calendar.client-id}")
    private String clientId;

    @Value("${google.calendar.client-secret}")
    private String clientSecret;

    @Value("${google.calendar.redirect-uri}")
    private String redirectUri;

    private final DoctorRepository doctorRepo;

    /**
     * 1. Construire l’URL OAuth2 pour autoriser l’accès Calendar
     */
    public String buildAuthUrl(Long doctorId) {
        return new GoogleAuthorizationCodeRequestUrl(
                clientId,
                redirectUri,
                List.of(CalendarScopes.CALENDAR_EVENTS))
                .setAccessType("offline")
                .setApprovalPrompt("force")  // 🔥 Ajout obligatoire pour forcer Google à redonner un refresh_token
                .setState(doctorId.toString())
                .build();

    }

    /**
     * 2. Gérer le callback OAuth2 de Google (« code » + « state »)
     */
    public void handleOAuthCallback(String code, String state) throws IOException {
        GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(
                new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                clientId,
                clientSecret,
                code,
                redirectUri)
                .execute();

        Long doctorId = Long.valueOf(state);
        Doctor doctor = doctorRepo.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found: " + doctorId));

        doctor.setGAccessToken(tokenResponse.getAccessToken());
        doctor.setGRefreshToken(tokenResponse.getRefreshToken());
        doctor.setGTokenExpiryMs(
                System.currentTimeMillis() + tokenResponse.getExpiresInSeconds() * 1_000L
        );
        doctorRepo.save(doctor);
    }

    /**
     * 3. Construire un client Calendar en auto-refreshant le token si nécessaire
     */
    private Calendar calendar(Doctor doctor) throws IOException {
        GoogleCredential credential = new GoogleCredential.Builder()
                .setTransport(new NetHttpTransport())
                .setJsonFactory(GsonFactory.getDefaultInstance())
                .setClientSecrets(clientId, clientSecret)
                .build()
                .setAccessToken(doctor.getGAccessToken())
                .setRefreshToken(doctor.getGRefreshToken())
                .setExpirationTimeMilliseconds(doctor.getGTokenExpiryMs());

        // Si le token expire sous 60s, on le rafraîchit et on enregistre
        if (credential.getExpiresInSeconds() != null
                && credential.getExpiresInSeconds() <= 60) {
            credential.refreshToken();
            doctor.setGAccessToken(credential.getAccessToken());
            doctor.setGTokenExpiryMs(credential.getExpirationTimeMilliseconds());
            doctorRepo.save(doctor);
        }

        return new Calendar.Builder(
                new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                credential)
                .setApplicationName("MediAssist")
                .build();
    }

    /**
     * 4. Créer un événement dans Google Calendar à partir d’un Appointment
     *    Retourne l’URL d’affichage (htmlLink)
     */
    public String createEvent(Appointment appointment) throws IOException {
        Doctor doctor = appointment.getSlot().getDoctor();
        Calendar client = calendar(doctor);

        Event event = new Event()
                .setSummary("Rendez-vous – " +
                        appointment.getPatient().getFirstName() + " " +
                        appointment.getPatient().getLastName())
                .setDescription("Motif : " + appointment.getMotif())
                .setStart(new EventDateTime()
                        .setDateTime(new DateTime(
                                appointment.getStart()
                                        .atZone(ZoneOffset.systemDefault())
                                        .toInstant().toEpochMilli())))
                .setEnd(new EventDateTime()
                        .setDateTime(new DateTime(
                                appointment.getEnd()
                                        .atZone(ZoneOffset.systemDefault())
                                        .toInstant().toEpochMilli())));

        Event created = client.events()
                .insert("primary", event)
                .execute();

        // On renvoie l'URL htmlLink qui permet d'afficher l'événement
        return created.getHtmlLink();
    }

    /**
     * 5. Supprimer l’événement Calendar via son ID (eid)
     */
    public void cancelEvent(Doctor doctor, String eventId) throws IOException {
        calendar(doctor).events().delete("primary", eventId).execute();
    }
}
