package com.event.event_management.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {

    private long totalUsers;
    private long totalEvents;
    private long totalTeams;
    private long totalRegistrations;
    private long totalAttendance;
}