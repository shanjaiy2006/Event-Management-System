package com.event.event_management.controller;

import com.event.event_management.entity.Attendance;
import com.event.event_management.entity.Event;
import com.event.event_management.entity.Registration;
import com.event.event_management.entity.User;
import com.event.event_management.repository.AttendanceRepository;
import com.event.event_management.repository.EventRepository;
import com.event.event_management.repository.RegistrationRepository;
import com.event.event_management.repository.UserRepository;
import com.event.event_management.service.AttendanceService;
import com.event.event_management.service.EmailService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Attendance APIs")
@RestController
@RequestMapping("/attendance")
@SecurityRequirement(name = "bearerAuth")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EmailService emailService;

    @PostMapping
    public Attendance markAttendance(
            @RequestParam String email,
            @RequestParam Long eventId) {

        // Check Event Exists
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() ->
                        new RuntimeException("Event not found"));

        // Check Student Exists
        User student = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Student not found"));

        // Check Student Role
        if (!student.getRole().name().equals("STUDENT")) {
            throw new RuntimeException("User is not a student");
        }

        // Check Registration
        boolean registered =
                registrationRepository
                        .existsByStudentEmailAndEventId(
                                email,
                                eventId);

        if (!registered) {
            throw new RuntimeException(
                    "Student is not registered for this event");
        }

        // Check Duplicate Attendance
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

        Attendance saved = attendanceRepository.save(attendance);

        emailService.sendEmail(
                email,
                "Attendance Marked Successfully",
                "Hello " + student.getName()
                        + ",\n\nYour attendance has been marked successfully.\n\n"
                        + "Event : " + event.getTitle()
                        + "\nVenue : " + event.getVenue()
                        + "\nDate : " + event.getEventDate()
                        + "\n\nThank You."
        );

        return saved;
    }

    @GetMapping
    public List<Attendance> getAllAttendance() {
        return attendanceService.getAllAttendance();
    }
}