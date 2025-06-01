package com.example.mobileproject.service;
import com.example.mobileproject.dto.DrugLiteDTO;
import com.example.mobileproject.entity.DrugReference;
import com.example.mobileproject.repository.DrugReferenceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class DrugService {

    private final DrugReferenceRepository repo;

    public DrugService(DrugReferenceRepository repo) {
        this.repo = repo;
    }

    // Opérations de lecture
    public Page<DrugReference> searchDrugs(String query, Pageable pageable) {
        return query.isBlank()
                ? repo.findAll(pageable)
                : repo.search(query, pageable);
    }

    public List<DrugLiteDTO> autocomplete(String term, int limit) {
        return repo.autocomplete(term, PageRequest.of(0, limit));
    }

    public DrugReference getDrugById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Drug not found"));
    }

    // Opérations d'écriture
    public DrugReference createDrug(DrugReference drug) {
        return repo.save(drug);
    }

    public DrugReference updateDrug(Long id, DrugReference updatedDrug) {
        DrugReference existingDrug = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Drug not found"));

        if (updatedDrug.getName() != null) {
            existingDrug.setName(updatedDrug.getName());
        }
        if (updatedDrug.getMolecule() != null) {
            existingDrug.setMolecule(updatedDrug.getMolecule());
        }
        if (updatedDrug.getAtcCode() != null) {
            existingDrug.setAtcCode(updatedDrug.getAtcCode());
        }

        return repo.save(existingDrug);
    }

    public void deleteDrug(Long id) {
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Drug not found");
        }
        repo.deleteById(id);
    }
}