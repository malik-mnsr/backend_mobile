package com.example.mobileproject.service;

import com.example.mobileproject.entity.Appointment;
import com.example.mobileproject.entity.Motif;
import com.example.mobileproject.entity.AppointmentStatus;
import com.example.mobileproject.entity.Patient;
import com.example.mobileproject.entity.Slot;
import com.example.mobileproject.repository.AppointmentRepository;
import com.example.mobileproject.repository.PatientRepository;
import com.example.mobileproject.repository.SlotRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final SlotRepository        slotRepo;
    private final PatientRepository     patientRepo;
    private final AppointmentRepository appRepo;
    private final GoogleCalendarService gCal;


    @Transactional
    public Appointment reserve(Long slotId, Long patientId, Motif motif) throws IOException {
        // 1. Verrouiller le slot
        Slot slot = slotRepo.findByIdForUpdate(slotId)
                .orElseThrow(() -> new EntityNotFoundException("Slot not found"));

        if (slot.isReserved()) {
            throw new IllegalStateException("Slot already reserved");
        }

        // 2. Charger le patient
        Patient patient = patientRepo.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Patient not found"));

        // 3. Créer et persister l’Appointment (sans lien Google pour l’instant)
        Appointment appt = Appointment.builder()
                .slot(slot)
                .patient(patient)
                .motif(motif)
                .status(AppointmentStatus.CONFIRMED)
                .build();
        appt = appRepo.save(appt);

        // 4. Marquer le slot comme réservé
        slot.setReserved(true);
        slot.setAppointment(appt);

        // 5. Créer l’événement dans Google Calendar et récupérer l’URL HTML
        String htmlLink = gCal.createEvent(appt);

        // 6. Stocker ce lien dans l’Appointment et mettre à jour la base
        appt.setGoogleEventLink(htmlLink);
        appt = appRepo.save(appt);

        return appt;
    }


    @Transactional
    public void cancel(Long apptId) throws IOException {
        Appointment appt = appRepo.findById(apptId)
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));

        // 1. Libérer le slot
        Slot slot = appt.getSlot();
        slot.setReserved(false);
        slot.setAppointment(null);

        // 2. Supprimer l’événement Google Calendar si lien présent
        String link = appt.getGoogleEventLink();
        if (link != null && link.contains("?eid=")) {
            String eid = link.substring(link.indexOf("?eid=") + 5);
            gCal.cancelEvent(slot.getDoctor(), eid);
        }

        // 3. Marquer le RDV annulé et persister
        appt.setStatus(AppointmentStatus.CANCELED);
        appRepo.save(appt);
    }


    public List<Appointment> findByDoctorId(Long doctorId) {
        return appRepo.findAllBySlot_Doctor_Id(doctorId);
    }


    public List<Appointment> findByPatientId(Long patientId) {
        return appRepo.findAllByPatient_Id(patientId);
    }
}
