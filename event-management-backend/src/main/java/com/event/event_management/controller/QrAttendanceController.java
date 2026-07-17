package com.event.event_management.controller;

import com.event.event_management.entity.Attendance;
import com.event.event_management.repository.AttendanceRepository;
import com.event.event_management.repository.EventRepository;
import com.event.event_management.repository.RegistrationRepository;
import com.event.event_management.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "QR Attendance APIs")
@RestController
@RequestMapping("/qr-attendance")
@SecurityRequirement(name = "bearerAuth")
public class QrAttendanceController {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private EventRepository eventRepository;

    @PostMapping
    public Attendance markAttendance(
            @RequestParam String email,
            @RequestParam Long eventId) {

        // Check event exists
        eventRepository.findById(eventId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Event not found"));

        // Check student registered
        boolean registered =
                registrationRepository
                        .existsByStudentEmailAndEventId(
                                email,
                                eventId);

        if (!registered) {
            throw new RuntimeException(
                    "Student not registered for this event");
        }

        // Check duplicate attendance
        if (attendanceRepository
                .existsByStudentEmailAndEventId(
                        email,
                        eventId)) {

            throw new RuntimeException(
                    "Attendance already marked");
        }

        Attendance attendance = new Attendance();

        attendance.setStudentEmail(email);
        attendance.setEventId(eventId);
        attendance.setPresent(true);

        Attendance saved =
                attendanceRepository.save(attendance);
        emailService.sendEmail(
                email,
                "Attendance Marked Successfully",
                "Your attendance has been marked for Event ID: "
                        + eventId
        );
        return saved;
    }
}