package com.event.event_management.repository;

import com.event.event_management.entity.Registration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistrationRepository
        extends JpaRepository<Registration, Long> {
    boolean existsByStudentEmailAndEventId(
            String studentEmail,
            Long eventId
    );

    long countByEventId(Long eventId);
}