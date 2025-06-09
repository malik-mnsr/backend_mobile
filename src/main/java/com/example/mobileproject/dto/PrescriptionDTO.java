package com.example.mobileproject.dto;

import java.time.Instant;
import java.util.List;

/**
 * DTO principal pour l’ordonnance.
 */
public record PrescriptionDTO(
        Long id,
        Integer medicalRecordId,
        String note,
        Instant dateCreated,
        List<MedicationLineDTO> medications
) {
    /* ---------- Mapping utilitaires ---------- */
    public static PrescriptionDTO from(com.example.mobileproject.entity.Prescription p) {
        List<MedicationLineDTO> meds = p.getMedications()
                .stream()
                .map(MedicationLineDTO::from)
                .toList();
        return new PrescriptionDTO(
                p.getId(),
                p.getMedicalRecord().getRecordId(),
                p.getNote(),
                p.getDateCreated(),
                meds
        );
    }

    /** Convertit le DTO (côté REST) en entité JPA. */
    public com.example.mobileproject.entity.Prescription toEntity() {
        com.example.mobileproject.entity.Prescription p =
                new com.example.mobileproject.entity.Prescription();
        p.setNote(note);

        // Convertir chaque ligne et rattacher
        medications.forEach(dto -> {
            var m = dto.toEntity();
            m.setPrescription(p);
            p.getMedications().add(m);
        });
        return p;
    }
}
