package com.event.event_management.controller;

import com.event.event_management.entity.Attendance;
import com.event.event_management.repository.AttendanceRepository;
import com.event.event_management.service.AttendanceService;
import com.event.event_management.service.EmailService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Attendance APIs")
@RestController
@RequestMapping("/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;
    @Autowired
    private AttendanceRepository attendanceRepository;
    @Autowired
    private EmailService emailService;

    @PostMapping
    public Attendance markAttendance(
            @RequestParam String email,
            @RequestParam Long eventId) {

        if(attendanceRepository
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
                "Hello,\n\nYour attendance has been marked successfully for Event ID: "
                        + eventId +
                        ".\n\nThank you."
        );

        return saved;
    }

    @GetMapping
    public List<Attendance> getAllAttendance() {
        return attendanceService.getAllAttendance();
    }
}