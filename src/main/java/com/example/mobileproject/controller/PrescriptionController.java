package com.example.mobileproject.controller;

import com.example.mobileproject.dto.PrescriptionDTO;
import com.example.mobileproject.entity.Prescription;
import com.example.mobileproject.service.PdfService;
import com.example.mobileproject.service.PrescriptionService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prescriptions")
@RequiredArgsConstructor
public class PrescriptionController {
    private final PdfService          pdfService;
    private final PrescriptionService service;

    /** Créer une ordonnance pour un dossier médical existant */
    @PostMapping("/record/{recordId}")
    public ResponseEntity<PrescriptionDTO> create(
            @PathVariable Integer recordId,
            @RequestBody PrescriptionDTO body) {

        var p = service.create(recordId, body);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(PrescriptionDTO.from(p));
    }

    /** Obtenir une ordonnance individuelle */
    @GetMapping("/{id}")
    public ResponseEntity<PrescriptionDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(
                PrescriptionDTO.from(service.get(id)));
    }

    /** Lister toutes les ordonnances d’un patient */
    @GetMapping("/patient/{patientId}")
    public List<PrescriptionDTO> list(@PathVariable Long patientId) {
        return service.listForPatient(patientId)
                .stream()
                .map(PrescriptionDTO::from)
                .toList();
    }

    /** Supprimer une ordonnance */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    /** Télécharger l’ordonnance en PDF */
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long id) {
        Prescription pres = service.get(id);
        byte[] pdf       = pdfService.prescriptionToPdf(pres);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename("ordonnance-" + id + ".pdf")
                        .build());

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdf);
    }
    // ajout dans PrescriptionController
    @PostMapping("/{id}/send-to-patient")
    public ResponseEntity<Void> sendToPatient(@PathVariable Long id)
            throws MessagingException {

        service.sendToPatient(id);
        return ResponseEntity.noContent().build();
    }


}
