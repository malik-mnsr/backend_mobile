package com.example.mobileproject.controller;

import com.example.mobileproject.dto.DrugLiteDTO;
import com.example.mobileproject.entity.DrugReference;
import com.example.mobileproject.repository.DrugReferenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/drugs")
@RequiredArgsConstructor
public class DrugController {

    private final DrugReferenceRepository repo;

    /* ---------- 1. Liste paginée (+ filtre texte facultatif) ---------- */
    @GetMapping
    public Page<DrugReference> list(
            @RequestParam(defaultValue = "") String q,
            @PageableDefault(size = 25, sort = "name") Pageable pageable) {

        return q.isBlank() ? repo.findAll(pageable) : repo.search(q, pageable);
    }

    /* ---------- 2. Auto-complétion très légère ---------- */
    @GetMapping("/autocomplete")
    public List<DrugLiteDTO> autocomplete(@RequestParam String term,
                                          @RequestParam(defaultValue = "10") int limit) {
        return repo.autocomplete(term, PageRequest.of(0, limit));
    }

    /* ---------- 3. Fiche détaillée par ID ---------- */
    @GetMapping("/{id}")
    public DrugReference byId(@PathVariable Long id) {
        return repo.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Drug not found"));
    }

   

    @PostMapping
    public DrugReference createDrug(@RequestBody DrugReference drug) {
        return repo.save(drug);
    }

    @PatchMapping("/{id}")
    public DrugReference updateDrug(@PathVariable Long id, @RequestBody DrugReference updatedDrug) {
        DrugReference drug = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Médicament non trouvé"));
        if (updatedDrug.getName() != null) drug.setName(updatedDrug.getName());
        if (updatedDrug.getMolecule() != null) drug.setMolecule(updatedDrug.getMolecule());
        if (updatedDrug.getAtcCode() != null) drug.setAtcCode(updatedDrug.getAtcCode());
        return repo.save(drug);
    }

    @DeleteMapping("/{id}")
    public void deleteDrug(@PathVariable Long id) {
        repo.deleteById(id);
    }

}
