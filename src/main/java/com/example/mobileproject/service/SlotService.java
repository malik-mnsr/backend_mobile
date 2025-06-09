package com.example.mobileproject.service;

import com.example.mobileproject.dto.SlotCreateDTO;
import com.example.mobileproject.dto.SlotDTO;
import com.example.mobileproject.entity.Doctor;
import com.example.mobileproject.entity.Slot;
import com.example.mobileproject.repository.DoctorRepository;
import com.example.mobileproject.repository.SlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SlotService {

    private final SlotRepository    slotRepo;
    private final DoctorRepository  doctorRepo;

    /**
     * Le médecin crée plusieurs créneaux en une seule requête.
     */
    @Transactional
    public List<SlotDTO> createSlots(Long doctorId, List<SlotCreateDTO> dtos) {
        Doctor doctor = doctorRepo.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Médecin introuvable"));

        List<Slot> slots = dtos.stream().map(dto -> {
            Slot s = new Slot();
            s.setDoctor(doctor);
            s.setStartAt(dto.startAt());
            s.setEndAt(dto.endAt());
            s.setReserved(false);
            return s;
        }).collect(Collectors.toList());

        return slotRepo.saveAll(slots)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Le patient ou l’IHM liste les créneaux libres d’un médecin pour une date donnée.
     */
    public List<SlotDTO> freeSlots(Long doctorId, LocalDate date) {
        return slotRepo.freeSlots(doctorId, date)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Helper de mapping Slot → SlotDTO.
     */
    public SlotDTO toDto(Slot s) {
        return new SlotDTO(
                s.getId(),
                s.getStartAt(),
                s.getEndAt(),
                s.isReserved()
        );
    }
}
