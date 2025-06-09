package com.example.mobileproject.dto;

/**
 * DTO d’une seule ligne de médicament.
 */
public record MedicationLineDTO(
        Long   id,
        String name,
        String dosage,
        String frequency,
        int    durationDays
) {
    /* ---------- Mapping utilitaires ---------- */
    public static MedicationLineDTO from(com.example.mobileproject.entity.MedicationLine m) {
        return new MedicationLineDTO(
                m.getId(),
                m.getName(),
                m.getDosage(),
                m.getFrequency(),
                m.getDurationDays()
        );
    }

    public com.example.mobileproject.entity.MedicationLine toEntity() {
        com.example.mobileproject.entity.MedicationLine m = new com.example.mobileproject.entity.MedicationLine();
        m.setName(name);
        m.setDosage(dosage);
        m.setFrequency(frequency);
        m.setDurationDays(durationDays);
        return m;
    }
}
