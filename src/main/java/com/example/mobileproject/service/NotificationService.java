package com.example.mobileproject.service;

import com.example.mobileproject.entity.Appointment;
import com.example.mobileproject.entity.Doctor;
import com.example.mobileproject.entity.WorkingMode;
import com.example.mobileproject.entity.Motif;
import com.example.mobileproject.repository.DoctorRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private final DoctorRepository doctorRepo;
    private final FirebaseMessaging fcm;

    /**
     * Envoie une notification push FCM au médecin si son mode le permet.
     */
    public void notifyDoctorOfRequest(Appointment appt) {
        Doctor doc = appt.getSlot().getDoctor();
        String token = doc.getFcmToken();

        // Ne pas notifier si pas de token, en "Do Not Disturb" ou "Absent"
        if (token == null
                || doc.getCurrentMode() == WorkingMode.DND
                || doc.getCurrentMode() == WorkingMode.ABSENT) {
            return;
        }

        // Construire le titre selon le motif
        String title;
        switch (appt.getMotif()) {
            case HOME_VISIT:
                title = "Nouvelle visite à domicile";
                break;
            case EMERGENCY:
                title = "Nouvelle urgence médicale";
                break;
            default:
                title = "Nouvelle demande de RDV";
        }

        // Corps = nom du patient + date + heure
        String body = appt.getPatient().getFirstName()
                + " " + appt.getPatient().getLastName()
                + " – " + appt.getStart().toString()
                + " à " + appt.getStart().toLocalTime();

        Message msg = Message.builder()
                .setToken(token)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .build();

        try {
            String response = fcm.send(msg);
            log.info("Notification sent: {}", response);
        } catch (FirebaseMessagingException e) {
            log.error("FCM error: {}", e.getMessage(), e);
        }
    }
}
