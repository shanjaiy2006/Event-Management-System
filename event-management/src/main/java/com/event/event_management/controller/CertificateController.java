package com.event.event_management.controller;

import com.event.event_management.entity.Registration;
import com.event.event_management.repository.AttendanceRepository;
import com.event.event_management.repository.RegistrationRepository;
import com.event.event_management.service.EmailService;
import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;

@Tag(name = "Certificate APIs")
@RestController
@RequestMapping("/certificate")
@SecurityRequirement(name = "bearerAuth")
public class CertificateController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Operation(
            summary = "Generate and Email Certificate",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/{name}/{email}")
    public String generateCertificate(
            @PathVariable String name,
            @PathVariable String email)
            throws Exception {

        // Check whether the student registered for an event
        Registration registration = registrationRepository
                .findByStudentNameAndStudentEmail(name, email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Student is not registered for any event"));

        // Check whether attendance is marked
        if (!attendanceRepository.existsByStudentEmailAndPresentTrue(email)) {
            throw new RuntimeException(
                    "Attendance not marked. Certificate cannot be generated.");
        }

        // Generate PDF
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        Document document = new Document(PageSize.A4);

        PdfWriter.getInstance(document, baos);

        document.open();

        document.add(new Paragraph("CERTIFICATE OF PARTICIPATION"));
        document.add(new Paragraph(" "));
        document.add(new Paragraph(
                "This certificate is proudly presented to"));
        document.add(new Paragraph(" "));
        document.add(new Paragraph(registration.getStudentName()));
        document.add(new Paragraph(" "));
        document.add(new Paragraph(
                "For successfully participating in the event."));
        document.add(new Paragraph(" "));
        document.add(new Paragraph(
                "Event ID : " + registration.getEventId()));

        document.close();

        byte[] pdfBytes = baos.toByteArray();

        // Send certificate
        emailService.sendCertificate(
                registration.getStudentEmail(),
                pdfBytes
        );

        return "Certificate generated and emailed successfully.";
    }
}