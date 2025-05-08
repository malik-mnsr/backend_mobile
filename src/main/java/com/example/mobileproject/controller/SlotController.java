package com.example.mobileproject.controller;

import java.time.LocalDate;
import java.util.List;

import com.example.mobileproject.service.SlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.example.mobileproject.dto.SlotDTO;
import com.example.mobileproject.dto.SlotCreateDTO;

@RestController
@RequestMapping("/api/slots")
@RequiredArgsConstructor
class SlotController {

    private final SlotService service;

    /* Médecin crée ses créneaux */
    @PostMapping("/doctor/{mid}")
    public List<SlotDTO> add(@PathVariable Long mid,
                             @RequestBody List<SlotCreateDTO> dto) {
        return service.createSlots(mid, dto);
    }

    /* Patient liste créneaux libres d’un jour */
    @GetMapping("/doctor/{mid}")
    public List<SlotDTO> free(@PathVariable Long mid,
                              @RequestParam LocalDate date) {
        return service.freeSlots(mid, date);
    }
}

