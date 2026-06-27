package com.event.event_management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.MimeMessageHelper;
import jakarta.mail.internet.MimeMessage;
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendCertificate(
            String to,
            byte[] pdfBytes
    ) throws Exception {

        MimeMessage message =
                mailSender.createMimeMessage();

        MimeMessageHelper helper =
                new MimeMessageHelper(
                        message,
                        true
                );

        helper.setTo(to);
        helper.setSubject("Certificate Generated");
        helper.setText(
                "Your participation certificate is attached."
        );

        helper.addAttachment(
                "certificate.pdf",
                new ByteArrayResource(pdfBytes)
        );

        mailSender.send(message);
    }

    public void sendCertificateEmail(
            String to,
            String eventName,
            byte[] pdfBytes) throws Exception {

        MimeMessage message =
                mailSender.createMimeMessage();

        MimeMessageHelper helper =
                new MimeMessageHelper(
                        message,
                        true
                );

        helper.setTo(to);
        helper.setSubject(
                "Certificate Generated Successfully"
        );

        helper.setText(
                "Your participation certificate is attached."
        );

        String fileName =
                to + "_" + eventName + ".pdf";

        helper.addAttachment(
                fileName,
                new ByteArrayResource(pdfBytes)
        );

        mailSender.send(message);
    }

    public void sendEmail(
            String to,
            String subject,
            String body) {

        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }
}