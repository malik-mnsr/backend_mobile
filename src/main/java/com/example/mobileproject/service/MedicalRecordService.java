// src/main/java/com/example/mobileproject/service/MedicalRecordService.java
package com.example.mobileproject.service;

import com.example.mobileproject.dto.MedicalRecordDTO;
import com.example.mobileproject.entity.Appointment;
import com.example.mobileproject.entity.MedicalRecord;
import com.example.mobileproject.entity.Motif;
import com.example.mobileproject.repository.AppointmentRepository;
import com.example.mobileproject.repository.MedicalRecordRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicalRecordService {

    private final MedicalRecordRepository recordRepo;
    private final AppointmentRepository  appointmentRepo;

    /* -------------------- CRUD principal -------------------- */

    @Transactional
    public MedicalRecordDTO createForAppointment(Long appointmentId,
                                                 MedicalRecordDTO dto) {

        Appointment appt = appointmentRepo.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Appointment not found: " + appointmentId));

        if (recordRepo.findByAppointment_Id(appointmentId).isPresent()) {
            throw new IllegalStateException(
                    "Record already exists for appointment " + appointmentId);
        }

        MedicalRecord rec = MedicalRecord.builder()
                .appointment(appt)
                .patient(appt.getPatient())
                .doctor(appt.getSlot().getDoctor())
                .recordType(Enum.valueOf(
                        com.example.mobileproject.entity.RecordType.class,
                        dto.recordType()))
                .title(dto.title())
                .description(dto.description())
                .content(dto.content())
                .build();

        return toDto(recordRepo.save(rec));
    }

    @Transactional(readOnly = true)
    public MedicalRecordDTO getByAppointment(Long appointmentId) {
        return recordRepo.findByAppointment_Id(appointmentId)
                .map(this::toDto)
                .orElseThrow(() -> new EntityNotFoundException(
                        "MedicalRecord not found for appointment " + appointmentId));
    }

    @Transactional
    public MedicalRecordDTO update(Integer recordId, MedicalRecordDTO dto) {
        MedicalRecord rec = recordRepo.findById(recordId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "MedicalRecord not found: " + recordId));

        rec.setRecordType(Enum.valueOf(
                com.example.mobileproject.entity.RecordType.class,
                dto.recordType()));
        rec.setTitle(dto.title());
        rec.setDescription(dto.description());
        rec.setContent(dto.content());

        return toDto(recordRepo.save(rec));
    }

    @Transactional(readOnly = true)
    public List<MedicalRecordDTO> historyForPatient(Long patientId) {
        return recordRepo.findByPatient_Id(patientId)
                .stream().map(this::toDto).toList();
    }

    @Transactional
    public void delete(Integer recordId) {
        if (!recordRepo.existsById(recordId)) {
            throw new EntityNotFoundException(
                    "MedicalRecord not found: " + recordId);
        }
        recordRepo.deleteById(recordId);
    }

    /* -------------------- Filtres pour « modes intelligents » -------------------- */


    @Transactional(readOnly = true)
    public List<MedicalRecordDTO> historyFiltered(Long docId, LocalDate date, Motif motif) {
        if (date != null) {
            // Utilise recordsForDay avec LocalDateTime
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.plusDays(1).atStartOfDay();

            return recordRepo.recordsForDay(docId, start, end, motif)
                    .stream().map(this::toDto).toList();
        }

        // Sinon, utilise recordsFiltered avec LocalDate null
        return recordRepo.recordsFiltered(docId, null, motif)
                .stream().map(this::toDto).toList();
    }


    /** Dossiers créés aujourd’hui (optionnellement filtrés par motif). */

    @Transactional(readOnly = true)
    public List<MedicalRecordDTO> todayRecords(Long doctorId, Motif motif) {
        LocalDate today = LocalDate.now();
        return recordRepo.recordsFiltered(doctorId, today, motif)
                .stream()
                .map(this::toDto)
                .toList();
    }



    /* -------------------- Mapper interne -------------------- */

    private MedicalRecordDTO toDto(MedicalRecord r) {
        return new MedicalRecordDTO(
                r.getRecordId(),
                r.getPatient().getId(),
                r.getDoctor().getId(),
                r.getAppointment() == null ? null : r.getAppointment().getId(),
                r.getRecordType().name(),
                r.getTitle(),
                r.getDescription(),
                r.getContent(),
                r.getDateCreated()
        );
    }
    /* ----------------------------------------------------------------
     *  Lecture simple d’un dossier par son id
     * ---------------------------------------------------------------- */
    @Transactional(readOnly = true)
    public MedicalRecordDTO getById(Integer recordId) {
        MedicalRecord rec = recordRepo.findById(recordId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "MedicalRecord not found: " + recordId));
        return toDto(rec);
    }

}
