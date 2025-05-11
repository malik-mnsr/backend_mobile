package com.example.mobileproject.service;

import com.example.mobileproject.dto.MedicalRecordDTO;
import com.example.mobileproject.entity.Appointment;
import com.example.mobileproject.entity.MedicalRecord;
import com.example.mobileproject.repository.AppointmentRepository;
import com.example.mobileproject.repository.MedicalRecordRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MedicalRecordService {

    private final MedicalRecordRepository recordRepo;
    private final AppointmentRepository  appointmentRepo;

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

        rec = recordRepo.save(rec);
        return toDto(rec);
    }

    @Transactional(readOnly = true)
    public MedicalRecordDTO getByAppointment(Long appointmentId) {
        MedicalRecord rec = recordRepo
                .findByAppointment_Id(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "MedicalRecord not found for appointment " + appointmentId));
        return toDto(rec);
    }

    @Transactional
    public MedicalRecordDTO update(Integer recordId,
                                   MedicalRecordDTO dto) {
        MedicalRecord rec = recordRepo.findById(recordId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "MedicalRecord not found: " + recordId));

        rec.setRecordType(Enum.valueOf(
                com.example.mobileproject.entity.RecordType.class,
                dto.recordType()));
        rec.setTitle(dto.title());
        rec.setDescription(dto.description());
        rec.setContent(dto.content());

        rec = recordRepo.save(rec);
        return toDto(rec);
    }

    @Transactional(readOnly = true)
    public List<MedicalRecordDTO> historyForPatient(Long patientId) {
        return recordRepo.findByPatient_Id(patientId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    @Transactional(readOnly = true)
    public MedicalRecordDTO getById(Integer recordId) {
        MedicalRecord rec = recordRepo.findById(recordId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "MedicalRecord not found: " + recordId));
        return toDto(rec);
    }

    @Transactional
    public void delete(Integer recordId) {
        if (!recordRepo.existsById(recordId)) {
            throw new EntityNotFoundException(
                    "MedicalRecord not found: " + recordId);
        }
        recordRepo.deleteById(recordId);
    }
    private MedicalRecordDTO toDto(MedicalRecord rec) {
        return new MedicalRecordDTO(
                rec.getRecordId(),
                rec.getPatient().getId(),
                rec.getDoctor().getId(),
                rec.getAppointment() != null
                        ? rec.getAppointment().getId()
                        : null,
                rec.getRecordType().name(),
                rec.getTitle(),
                rec.getDescription(),
                rec.getContent(),
                rec.getDateCreated()
        );
    }
}
