// src/main/java/com/example/mobileproject/service/PdfService.java
package com.example.mobileproject.service;

import com.example.mobileproject.entity.Prescription;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@Service
public class PdfService {

    /* ---------- PUBLIC ---------- */

    public byte[] prescriptionToPdf(Prescription p) {
        String html = buildHtml(p);
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            new PdfRendererBuilder()
                    .withHtmlContent(html, null)
                    .toStream(out)
                    .run();
            return out.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("PDF generation failed", e);
        }
    }

    /* ---------- PRIVATE ---------- */

    private String buildHtml(Prescription p) {

        var doc  = p.getMedicalRecord().getDoctor();
        var pat  = p.getMedicalRecord().getPatient();
        var date = p.getDateCreated()
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDate()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        /* tableau médicaments */
        String medsHtml = p.getMedications().stream()
                .map(m -> """
                    <tr>
                      <td><b>%s</b></td>
                      <td>%s</td>
                      <td>%s</td>
                       <td>%d&#160;j.</td>
                    </tr>
                    """.formatted(
                        safe(m.getName()),
                        safe(m.getDosage()),
                        safe(m.getFrequency()),
                        m.getDurationDays()))
                .collect(Collectors.joining());

        return """
        <html>
        <head>
          <meta charset="utf-8"/>
          <style>
              @page { size: A4; margin: 2cm 2.5cm 2cm 2.5cm; }
              body  { font-family: DejaVu Sans, sans-serif; font-size: 12px; }
              h1    { color:#1976D2; margin-bottom:0; }
              .small{ font-size:10px; }
              table { width:100%%; border-collapse:collapse; margin-top:1em; }
              th,td{ padding:4px; border-bottom:1px solid #ccc; }
          </style>
        </head>
        <body>

        <!-- EN-TÊTE MÉDECIN -->
        <h1>Dr %s %s</h1>
        <p><b>%s</b></p>
        <p>%s<br/>Tél&#160;: %s</p>

        <hr/>

        <!-- PATIENT & DATE -->
        <p><b>Patient&#160;:</b> %s %s<br/>
           <b>Date&#160;:</b> %s
        </p>

        <!-- TABLEAU DES MÉDICAMENTS -->
        <table>
          <thead style="background:#E3F2FD;">
            <tr>
              <th>Médicament</th><th>Dosage</th>
              <th>Fréquence</th><th>Durée</th>
            </tr>
          </thead>
          <tbody>
            %s
          </tbody>
        </table>

        <!-- NOTE LIBRE -->
        <p style="margin-top:1em;"><i>%s</i></p>

        <!-- SIGNATURE -->
        <p style="margin-top:2.5em;">
           Signé électroniquement le %s
        </p>

        <!-- FOOTER -->
        <p class="small" style="position:fixed;bottom:1cm;">
          En cas d’urgence, composez le 15 – Document généré par MediAssist
        </p>

        </body>
        </html>
        """.formatted(
                safe(doc.getFirstName()), safe(doc.getLastName()),
                safe(doc.getSpecialty() == null ? "" : doc.getSpecialty().toUpperCase()),
                safe(doc.getEmail() == null ? "" : doc.getEmail()),        // ← plus d'adresse
                safe(doc.getPhone() == null ? "" : doc.getPhone()),

                safe(pat.getFirstName()), safe(pat.getLastName()),
                date,

                medsHtml,

                safe(p.getNote() == null ? "" : p.getNote()),
                date
        );
    }


    private String safe(String s) {
        return s == null ? "" : s.replace("%", "%%");
    }
}
