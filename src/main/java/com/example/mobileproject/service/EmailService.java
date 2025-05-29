package com.example.mobileproject.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendPrescriptionPdf(String to,
                                    String patientName,
                                    byte[] pdf,
                                    String fileName) throws MessagingException {

        MimeMessage msg = mailSender.createMimeMessage();
        MimeMessageHelper helper =
                new MimeMessageHelper(msg, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject("Votre ordonnance médicale");
        helper.setText("""
                Bonjour %s,

                Veuillez trouver en pièce jointe votre ordonnance.

                — MediAssist
                """.formatted(patientName));

        helper.addAttachment(fileName,
                new ByteArrayResource(pdf));

        mailSender.send(msg);
    }
}
