package com.event.event_management.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // ===================== SEND NORMAL EMAIL =====================

    @Retryable(
            retryFor = Exception.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 3000)
    )
    public void sendEmail(
            String to,
            String subject,
            String body) {

        System.out.println("Trying to send Email...");

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);

        System.out.println("Email sent successfully.");
    }

    @Recover
    public void recoverEmail(
            Exception e,
            String to,
            String subject,
            String body) {

        System.out.println("======================================");
        System.out.println("Retry Mechanism Executed");
        System.out.println("Email failed after 3 attempts.");
        System.out.println("Recipient : " + to);
        System.out.println("Reason : " + e.getMessage());
        System.out.println("======================================");
    }

    // ===================== SEND CERTIFICATE =====================

    @Retryable(
            retryFor = Exception.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 3000)
    )
    public void sendCertificate(
            String to,
            byte[] pdfBytes) throws Exception {

        System.out.println("Trying to send Certificate...");

        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper =
                new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setSubject("Certificate Generated");
        helper.setText("Your participation certificate is attached.");

        helper.addAttachment(
                "certificate.pdf",
                new ByteArrayResource(pdfBytes)
        );

        mailSender.send(message);

        System.out.println("Certificate sent successfully.");
    }

    @Recover
    public void recoverCertificate(
            Exception e,
            String to,
            byte[] pdfBytes) {

        System.out.println("======================================");
        System.out.println("Retry Mechanism Executed");
        System.out.println("Certificate Email failed after 3 attempts.");
        System.out.println("Recipient : " + to);
        System.out.println("Reason : " + e.getMessage());
        System.out.println("======================================");
    }

    // ===================== SEND EVENT CERTIFICATE =====================

    @Retryable(
            retryFor = Exception.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 3000)
    )
    public void sendCertificateEmail(
            String to,
            String eventName,
            byte[] pdfBytes) throws Exception {

        System.out.println("Trying to send Event Certificate...");

        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper =
                new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setSubject("Certificate Generated Successfully");
        helper.setText("Your participation certificate is attached.");

        String fileName = to + "_" + eventName + ".pdf";

        helper.addAttachment(
                fileName,
                new ByteArrayResource(pdfBytes)
        );

        mailSender.send(message);

        System.out.println("Event Certificate sent successfully.");
    }

    @Recover
    public void recoverCertificateEmail(
            Exception e,
            String to,
            String eventName,
            byte[] pdfBytes) {

        System.out.println("======================================");
        System.out.println("Retry Mechanism Executed");
        System.out.println("Event Certificate Email failed after 3 attempts.");
        System.out.println("Recipient : " + to);
        System.out.println("Reason : " + e.getMessage());
        System.out.println("======================================");
    }
}