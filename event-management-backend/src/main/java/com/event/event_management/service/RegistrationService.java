package com.event.event_management.service;

import com.event.event_management.repository.AttendanceRepository;

import com.event.event_management.entity.Event;
import com.event.event_management.entity.Registration;
import com.event.event_management.repository.EventRepository;
import com.event.event_management.repository.RegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class RegistrationService {

    @Autowired
    private RegistrationRepository registrationRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private EmailService emailService;




    @Autowired
    private AttendanceRepository attendanceRepository;

    public List<Registration> getAllRegistrations() {
        // Return only registrations for valid active events
        List<Registration> allRegistrations = registrationRepository.findAll();
        LocalDate now = LocalDate.now();
        
        return allRegistrations.stream().filter(reg -> {
            return eventRepository.findById(reg.getEventId())
                .map(event -> !now.isAfter(event.getRegistrationDeadline()))
                .orElse(false); // If event not found, it's invalid
        }).toList();
    }

    @org.springframework.scheduling.annotation.Scheduled(cron = "0 0 * * * *") // Run every hour
    @org.springframework.transaction.annotation.Transactional
    public void cleanExpiredRegistrations() {
        List<Registration> allRegistrations = registrationRepository.findAll();
        LocalDate now = LocalDate.now();

        for (Registration reg : allRegistrations) {
            boolean isEventExpiredOrDeleted = eventRepository.findById(reg.getEventId())
                    .map(event -> now.isAfter(event.getRegistrationDeadline()))
                    .orElse(true);

            if (isEventExpiredOrDeleted) {
                boolean hasAttendance = attendanceRepository.existsByStudentEmailAndEventId(
                        reg.getStudentEmail(), reg.getEventId());
                
                // Remove pending registration if they didn't attend
                if (!hasAttendance) {
                    registrationRepository.delete(reg);
                }
            }
        }
    }

    public Registration register(Registration registration) {

        eventRepository.findById(
                        registration.getEventId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Event not found"));
        Event event = eventRepository
                .findById(registration.getEventId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Event not found"));

        if (LocalDate.now().isAfter(
                event.getRegistrationDeadline())) {

            throw new RuntimeException(
                    "Registration deadline is over");
        }

        long registrationCount =
                registrationRepository
                        .countByEventId(
                                registration.getEventId());

        if (registrationCount >=
                event.getMaxCapacity()) {

            throw new RuntimeException(
                    "Event is full");
        }

        boolean alreadyExists =
                registrationRepository
                        .existsByStudentEmailAndEventId(
                                registration.getStudentEmail(),
                                registration.getEventId()
                        );

        if (alreadyExists) {
            throw new RuntimeException(
                    "Already registered for this event"
            );
        }

        Registration saved =
                registrationRepository.save(registration);

        emailService.sendEmail(
                registration.getStudentEmail(),
                "Event Registration Successful",
                "You have successfully registered for Event ID: "
                        + registration.getEventId()
        );

        return saved;
    }

    public void unregister(String email, Long eventId) {
        Registration registration = registrationRepository
                .findByStudentEmailAndEventId(email, eventId)
                .orElseThrow(() -> new RuntimeException("Registration not found"));
        registrationRepository.delete(registration);
    }
}