package com.event.event_management.service;

import com.event.event_management.entity.Team;
import com.event.event_management.entity.Event;
import com.event.event_management.repository.TeamRepository;
import com.event.event_management.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TeamService {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private EventRepository eventRepository;

    public Team createTeam(Team team, String email) {

        team.setTeamCode(
                "TEAM-" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 6)
        );
        team.getMembers().add(email);

        return teamRepository.save(team);
    }

    public Team joinTeam(String code, String email) {
        Team team = teamRepository.findByTeamCode(code)
                .orElseThrow(() -> new RuntimeException("Team not found"));
        team.getMembers().add(email);
        return teamRepository.save(team);
    }

    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    public List<Team> getTeamsByMemberEmail(String email) {
        List<Team> allTeams = teamRepository.findByMembersContaining(email);
        return allTeams.stream().filter(team -> {
            if (team.getEventId() == null) return true;
            Event event = eventRepository.findById(team.getEventId()).orElse(null);
            if (event == null) return true;
            return !LocalDate.now().isAfter(event.getEventDate());
        }).collect(Collectors.toList());
    }
}