package com.event.event_management.repository;

import com.event.event_management.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceRepository
        extends JpaRepository<Attendance, Long> {
    boolean existsByStudentEmailAndEventId(
            String studentEmail,
            Long eventId
    );

    boolean existsByStudentEmailAndPresentTrue(String studentEmail);
    
    java.util.Optional<Attendance> findByStudentEmailAndEventId(String studentEmail, Long eventId);

}