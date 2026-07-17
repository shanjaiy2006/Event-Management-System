package com.event.event_management.service;

import com.event.event_management.entity.Attendance;
import com.event.event_management.entity.Registration;
import com.event.event_management.repository.AttendanceRepository;
import com.event.event_management.repository.EventRepository;
import com.event.event_management.repository.RegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;
    @Autowired
    private RegistrationRepository registrationRepository;
    @Autowired
    private EventRepository eventRepository;

    public Attendance markAttendance(Attendance attendance) {
        if(attendanceRepository.existsByStudentEmailAndEventId(
                attendance.getStudentEmail(),
                attendance.getEventId())) {

            throw new RuntimeException(
                    "Attendance already marked"
            );
        }

        eventRepository.findById(attendance.getEventId())
                .orElseThrow(() ->
                        new RuntimeException("Event not found"));

        boolean registered =
                registrationRepository
                        .existsByStudentEmailAndEventId(
                                attendance.getStudentEmail(),
                                attendance.getEventId());

        if(!registered){
            throw new RuntimeException(
                    "Student not registered for event"
            );
        }



        return attendanceRepository.save(attendance);
    }

    public List<Attendance> getAllAttendance() {
        return attendanceRepository.findAll();
    }

    public void unmarkAttendance(String email, Long eventId) {
        Attendance attendance = attendanceRepository
                .findByStudentEmailAndEventId(email, eventId)
                .orElseThrow(() -> new RuntimeException("Attendance record not found"));
        
        attendanceRepository.delete(attendance);
    }
}