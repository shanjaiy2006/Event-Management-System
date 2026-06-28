package com.event.event_management.controller;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.event.event_management.service.QrCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Tag(name = "QR APIs")
@RestController
@RequestMapping("/qr")
@SecurityRequirement(name = "bearerAuth")
public class QrController {

    @Autowired
    private QrCodeService qrCodeService;

    @GetMapping(value = "/{email}/{eventId}",
            produces = MediaType.IMAGE_PNG_VALUE)

    public byte[] generateQr(
            @PathVariable String email,
            @PathVariable Long eventId)
            throws Exception {

        String qrData = email + ":" + eventId;

        return qrCodeService.generateQRCode(qrData);
    }
}

