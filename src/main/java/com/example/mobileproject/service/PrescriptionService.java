package com.example.mobileproject.service;

import com.example.mobileproject.dto.PrescriptionDTO;
import com.example.mobileproject.entity.MedicalRecord;
import com.example.mobileproject.entity.Prescription;
import com.example.mobileproject.repository.MedicalRecordRepository;
import com.example.mobileproject.repository.PrescriptionRepository;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PrescriptionService {

    private final PrescriptionRepository repo;
    private final MedicalRecordRepository recordRepo;

    private final PdfService             pdfService;
    private final EmailService           emailService;
    /* ---------- CREATE ---------- */
    @Transactional
    public Prescription create(Integer recordId, PrescriptionDTO dto) {
        MedicalRecord rec = recordRepo.findById(recordId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "MedicalRecord not found: " + recordId));

        Prescription p = dto.toEntity();
        p.setMedicalRecord(rec);
        p.getMedications().forEach(m -> m.setPrescription(p));

        return repo.save(p);
    }

    /* ---------- READ ---------- */
    public Prescription get(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Prescription not found: " + id));
    }

    public List<Prescription> listForPatient(Long patientId) {
        return repo.findByMedicalRecord_Patient_Id(patientId);
    }

    /* ---------- DELETE ---------- */
    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new EntityNotFoundException("Prescription not found: " + id);
        }
        repo.deleteById(id);
    }
    /** Envoie l’ordonnance par mail au patient */
    public void sendToPatient(Long prescriptionId) throws MessagingException {

        Prescription p = repo.findById(prescriptionId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Prescription not found: " + prescriptionId));

        String email = p.getMedicalRecord().getPatient().getEmail();
        if (email == null || email.isBlank()) {
            throw new IllegalStateException("Patient email missing");
        }

        byte[] pdf = pdfService.prescriptionToPdf(p);

        emailService.sendPrescriptionPdf(
                email,
                p.getMedicalRecord().getPatient().getFirstName(),
                pdf,
                "ordonnance-" + prescriptionId + ".pdf"
        );
    }
}
