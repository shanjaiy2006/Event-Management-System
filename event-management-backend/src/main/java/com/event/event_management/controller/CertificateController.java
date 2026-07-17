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

    @Autowired
    private com.event.event_management.service.QrCodeService qrCodeService;
    
    @Autowired
    private com.event.event_management.repository.EventRepository eventRepository;

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
        // Note: The previous logic grabbed the first registration.
        // For a robust system it should take eventId, but I will maintain the signature.
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
        
        com.event.event_management.entity.Event event = eventRepository.findById(registration.getEventId())
            .orElseThrow(() -> new RuntimeException("Event not found"));

        // Generate PDF
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // Landscape mode for certificate
        Document document = new Document(PageSize.A4.rotate());

        PdfWriter writer = PdfWriter.getInstance(document, baos);

        document.open();

        // Professional Border
        com.itextpdf.text.pdf.PdfContentByte canvas = writer.getDirectContent();
        canvas.rectangle(20, 20, PageSize.A4.rotate().getWidth() - 40, PageSize.A4.rotate().getHeight() - 40);
        canvas.setLineWidth(3);
        canvas.setColorStroke(com.itextpdf.text.BaseColor.DARK_GRAY);
        canvas.stroke();
        
        canvas.rectangle(25, 25, PageSize.A4.rotate().getWidth() - 50, PageSize.A4.rotate().getHeight() - 50);
        canvas.setLineWidth(1);
        canvas.stroke();

        com.itextpdf.text.Font titleFont = com.itextpdf.text.FontFactory.getFont(com.itextpdf.text.FontFactory.TIMES_BOLD, 36, com.itextpdf.text.BaseColor.BLACK);
        com.itextpdf.text.Font subtitleFont = com.itextpdf.text.FontFactory.getFont(com.itextpdf.text.FontFactory.TIMES_ROMAN, 18, com.itextpdf.text.BaseColor.DARK_GRAY);
        com.itextpdf.text.Font nameFont = com.itextpdf.text.FontFactory.getFont(com.itextpdf.text.FontFactory.TIMES_ITALIC, 32, com.itextpdf.text.BaseColor.BLUE);
        com.itextpdf.text.Font textFont = com.itextpdf.text.FontFactory.getFont(com.itextpdf.text.FontFactory.TIMES_ROMAN, 14, com.itextpdf.text.BaseColor.BLACK);

        Paragraph emptyLines = new Paragraph("\n\n");
        document.add(emptyLines);

        Paragraph title = new Paragraph("CERTIFICATE OF PARTICIPATION", titleFont);
        title.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
        document.add(title);
        
        document.add(new Paragraph("\n"));

        Paragraph subtitle = new Paragraph("This certificate is proudly presented to", subtitleFont);
        subtitle.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
        document.add(subtitle);

        document.add(new Paragraph("\n"));

        Paragraph studentName = new Paragraph(registration.getStudentName(), nameFont);
        studentName.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
        document.add(studentName);

        document.add(new Paragraph("\n"));

        Paragraph reason = new Paragraph("For successfully participating and demonstrating excellence in", textFont);
        reason.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
        document.add(reason);
        
        document.add(new Paragraph("\n"));
        
        Paragraph eventName = new Paragraph(event.getTitle(), com.itextpdf.text.FontFactory.getFont(com.itextpdf.text.FontFactory.TIMES_BOLD, 20, com.itextpdf.text.BaseColor.BLACK));
        eventName.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
        document.add(eventName);

        Paragraph dateText = new Paragraph("held on " + event.getEventDate() + " at " + event.getVenue(), textFont);
        dateText.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
        document.add(dateText);

        document.add(new Paragraph("\n\n"));
        
        // Add QR Code
        String qrData = "VERIFIED:" + registration.getStudentEmail() + ":" + event.getId();
        byte[] qrBytes = qrCodeService.generateQRCode(qrData);
        com.itextpdf.text.Image qrImage = com.itextpdf.text.Image.getInstance(qrBytes);
        qrImage.scaleAbsolute(80, 80);
        qrImage.setAbsolutePosition(100, 50);
        document.add(qrImage);
        
        // Organizer signature area
        canvas.beginText();
        canvas.setFontAndSize(com.itextpdf.text.pdf.BaseFont.createFont(com.itextpdf.text.pdf.BaseFont.TIMES_ROMAN, com.itextpdf.text.pdf.BaseFont.CP1252, com.itextpdf.text.pdf.BaseFont.NOT_EMBEDDED), 14);
        canvas.showTextAligned(com.itextpdf.text.Element.ALIGN_CENTER, "_______________________", PageSize.A4.rotate().getWidth() - 150, 90, 0);
        canvas.showTextAligned(com.itextpdf.text.Element.ALIGN_CENTER, event.getCreatedBy() + " (Organizer)", PageSize.A4.rotate().getWidth() - 150, 70, 0);
        canvas.endText();
        
        // Certificate ID
        canvas.beginText();
        canvas.setFontAndSize(com.itextpdf.text.pdf.BaseFont.createFont(com.itextpdf.text.pdf.BaseFont.HELVETICA, com.itextpdf.text.pdf.BaseFont.CP1252, com.itextpdf.text.pdf.BaseFont.NOT_EMBEDDED), 10);
        canvas.showTextAligned(com.itextpdf.text.Element.ALIGN_LEFT, "Certificate ID: CERT-" + registration.getId(), 50, 35, 0);
        canvas.endText();

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