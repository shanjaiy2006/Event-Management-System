package com.event.event_management.service;

import com.event.event_management.dto.DashboardResponse;
import com.event.event_management.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    public DashboardResponse getDashboard() {

        return new DashboardResponse(
                userRepository.count(),
                eventRepository.count(),
                teamRepository.count(),
                registrationRepository.count(),
                attendanceRepository.count()
        );
    }
}