package com.example.mobileproject.service;

import com.example.mobileproject.dto.AppointmentDTO;
import com.example.mobileproject.entity.*;
import com.example.mobileproject.repository.AppointmentRepository;
import com.example.mobileproject.repository.PatientRepository;
import com.example.mobileproject.repository.SlotRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentService {

    private final SlotRepository        slotRepo;
    private final PatientRepository     patientRepo;
    private final AppointmentRepository appRepo;
    private final GoogleCalendarService gCal;
    private final NotificationService   notificationService;
    private final SlotService           slotService;
    private final PatientService        patientService;

    /**
     * Réserve un slot selon le motif :
     * - auto‐confirmé (CONSULTATION, TELECONSULTATION)
     * - en attente (HOME_VISIT, EMERGENCY) + notification FCM
     */
    @Transactional
    public Appointment reserve(Long slotId, Long patientId, Motif motif) {
        Slot slot = slotRepo.findByIdForUpdate(slotId)
                .orElseThrow(() -> new EntityNotFoundException("Slot not found: " + slotId));
        if (slot.isReserved()) {
            throw new IllegalStateException("Slot already reserved");
        }

        Patient patient = patientRepo.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Patient not found: " + patientId));

        boolean autoConfirm = switch (motif) {
            case CONSULTATION, TELECONSULTATION -> true;
            default -> false;
        };

        Appointment appt = Appointment.builder()
                .slot(slot)
                .patient(patient)
                .motif(motif)
                .status(autoConfirm
                        ? AppointmentStatus.CONFIRMED
                        : AppointmentStatus.PENDING)
                .build();
        appt = appRepo.save(appt);

        if (autoConfirm) {
            slot.setReserved(true);
            slot.setAppointment(appt);
            try {
                String link = gCal.createEvent(appt);
                appt.setGoogleEventLink(link);
                appt = appRepo.save(appt);
            } catch (IOException e) {
                log.error("Google Calendar error, created without link", e);
            }
        } else {
            notificationService.notifyDoctorOfRequest(appt);
        }

        return appt;
    }

    /**
     * Annule un rendez-vous (libère le slot, supprime l’événement Calendar, passe CANCELED)
     */
    @Transactional
    public void cancel(Long apptId) throws IOException {
        Appointment appt = appRepo.findById(apptId)
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found: " + apptId));

        Slot slot = appt.getSlot();
        slot.setReserved(false);
        slot.setAppointment(null);

        String link = appt.getGoogleEventLink();
        if (link != null && link.contains("?eid=")) {
            String eid = link.substring(link.indexOf("?eid=") + 5);
            gCal.cancelEvent(slot.getDoctor(), eid);
        }

        appt.setStatus(AppointmentStatus.CANCELED);
        appRepo.save(appt);
    }

    /**
     * Accepte une demande PENDING : confirme, réserve le slot et crée l’événement Google Calendar.
     */
    @Transactional
    public Appointment accept(Long apptId) throws IOException {
        Appointment appt = appRepo.findById(apptId)
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found: " + apptId));

        if (appt.getStatus() != AppointmentStatus.PENDING) {
            throw new IllegalStateException("Appointment not in PENDING state");
        }

        Slot slot = appt.getSlot();
        slot.setReserved(true);
        slot.setAppointment(appt);

        appt.setStatus(AppointmentStatus.CONFIRMED);
        try {
            String link = gCal.createEvent(appt);
            appt.setGoogleEventLink(link);
        } catch (IOException e) {
            log.error("Google Calendar error on accept", e);
        }

        return appRepo.save(appt);
    }

    /**
     * Rejette une demande PENDING : passe CANCELED, le slot reste libre.
     */
    @Transactional
    public void reject(Long apptId) {
        Appointment appt = appRepo.findById(apptId)
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found: " + apptId));

        if (appt.getStatus() != AppointmentStatus.PENDING) {
            throw new IllegalStateException("Appointment not in PENDING state");
        }

        appt.setStatus(AppointmentStatus.CANCELED);
        appRepo.save(appt);
    }

    public List<Appointment> findByDoctorId(Long doctorId) {
        return appRepo.findAllBySlot_Doctor_Id(doctorId);
    }

    public List<Appointment> findByPatientId(Long patientId) {
        return appRepo.findAllByPatient_Id(patientId);
    }

    /** Mapping manuel vers DTO */
    public AppointmentDTO toDto(Appointment a) {
        return new AppointmentDTO(
                a.getId(),
                slotService.toDto(a.getSlot()),
                patientService.toDto(a.getPatient()),
                a.getMotif().name(),
                a.getStatus().name(),
                a.getGoogleEventLink()
        );
    }
}
