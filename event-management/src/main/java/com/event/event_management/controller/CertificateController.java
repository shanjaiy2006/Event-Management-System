package com.event.event_management.controller;

import com.event.event_management.service.EmailService;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.io.ByteArrayOutputStream;

import com.event.event_management.service.EmailService;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@Tag(name = "Certificate APIs")
@RestController
@RequestMapping("/certificate")
public class CertificateController {
    @Autowired
    private EmailService emailService;




    @GetMapping("/{name}/{email}")
    public String generateCertificate(
            @PathVariable String name,
            @PathVariable String email)
            throws Exception {

        ByteArrayOutputStream baos =
                new ByteArrayOutputStream();

        Document document =
                new Document(PageSize.A4);

        PdfWriter.getInstance(
                document,
                baos
        );

        document.open();

        document.add(
                new Paragraph(
                        "CERTIFICATE OF PARTICIPATION"
                )
        );

        document.add(
                new Paragraph(
                        "Awarded to " + name
                )
        );

        document.close();

        byte[] pdfBytes =
                baos.toByteArray();

        emailService.sendCertificate(
                email,
                pdfBytes
        );

        return "Certificate sent successfully!";
    }
}