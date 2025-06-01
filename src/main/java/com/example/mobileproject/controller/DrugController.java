package com.example.mobileproject.controller;

import com.example.mobileproject.dto.DrugLiteDTO;
import com.example.mobileproject.entity.DrugReference;
import com.example.mobileproject.repository.DrugReferenceRepository;
import com.example.mobileproject.service.DrugService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;
@RestController
@RequestMapping("/api/drugs")
public class DrugController {

    private final DrugService drugService;

    public DrugController(DrugService drugService) {
        this.drugService = drugService;
    }

    @GetMapping
    public Page<DrugReference> list(
            @RequestParam(defaultValue = "") String q,
            @PageableDefault(size = 25, sort = "name") Pageable pageable) {
        return drugService.searchDrugs(q, pageable);
    }

    @GetMapping("/autocomplete")
    public List<DrugLiteDTO> autocomplete(
            @RequestParam String term,
            @RequestParam(defaultValue = "10") int limit) {
        return drugService.autocomplete(term, limit);
    }

    @GetMapping("/{id}")
    public DrugReference byId(@PathVariable Long id) {
        return drugService.getDrugById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DrugReference createDrug(@RequestBody DrugReference drug) {
        return drugService.createDrug(drug);
    }

    @PatchMapping("/{id}")
    public DrugReference updateDrug(
            @PathVariable Long id,
            @RequestBody DrugReference updatedDrug) {
        return drugService.updateDrug(id, updatedDrug);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDrug(@PathVariable Long id) {
        drugService.deleteDrug(id);
    }
}