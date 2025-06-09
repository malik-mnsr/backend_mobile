package com.example.mobileproject.controller;



import com.example.mobileproject.entity.*;
import com.example.mobileproject.repository.DoctorRepository;
import com.example.mobileproject.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final DoctorRepository doctorRepository;
    public NotificationController(NotificationService notificationService, DoctorRepository doctorRepository) {
        this.notificationService = notificationService;
        this.doctorRepository = doctorRepository;
    }

    /**
     * Endpoint to trigger a doctor notification
     *
     * @param appointment The appointment details
     * @return Response indicating success or failure
     */
    @PostMapping("/notify-doctor")
    public ResponseEntity<String> notifyDoctor(@RequestBody Appointment appointment) {
        try {
            notificationService.notifyDoctorOfRequest(appointment);
            return ResponseEntity.ok("Notification sent successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Failed to send notification: " + e.getMessage());
        }
    }

    /**
     * Endpoint to test notification with custom message
     *
     * @param request Contains doctorId and custom message
     * @return Response indicating success or failure
     */
    @PostMapping("/test-notification")
    public ResponseEntity<String> sendTestNotification(@RequestBody TestNotificationRequest request) {
        try {
            Appointment testAppt = new Appointment();
            testAppt.setMotif(request.getMotif() != null ? request.getMotif() : Motif.CONSULTATION);

            // Fetch doctor by ID from DB (you need DoctorRepository injected)
            Doctor doctor = doctorRepository.findById(request.getDoctorId())
                    .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));

            // Build slot and set doctor
            // create slot and set doctor
            Slot slot = new Slot();
            slot.setDoctor(doctor);
            slot.setStartAt(LocalDateTime.now());  // <== set startAt here, critical!
            slot.setEndAt(LocalDateTime.now().plusMinutes(30)); // optionally set endAt

            testAppt.setSlot(slot);

// patient etc.
            Patient patient = new Patient();
            patient.setFirstName("Test");
            patient.setLastName("User");
            testAppt.setPatient(patient);

// no need to call testAppt.setStart() because start is derived from slot

            notificationService.notifyDoctorOfRequest(testAppt);

            return ResponseEntity.ok("Test notification sent successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Failed to send test notification: " + e.getMessage());
        }
    }


    // Request DTO for test notifications
    static class TestNotificationRequest {
        private Long doctorId;
        private Motif motif;
        private String customMessage;

        // Getters and setters
        public Long getDoctorId() {
            return doctorId;
        }

        public void setDoctorId(Long doctorId) {
            this.doctorId = doctorId;
        }

        public Motif getMotif() {
            return motif;
        }

        public void setMotif(Motif motif) {
            this.motif = motif;
        }

        public String getCustomMessage() {
            return customMessage;
        }

        public void setCustomMessage(String customMessage) {
            this.customMessage = customMessage;
        }
    }
}