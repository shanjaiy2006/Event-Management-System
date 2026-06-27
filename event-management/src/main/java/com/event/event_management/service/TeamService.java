package com.event.event_management.service;

import com.event.event_management.entity.Team;
import com.event.event_management.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TeamService {

    @Autowired
    private TeamRepository teamRepository;

    public Team createTeam(Team team) {

        team.setTeamCode(
                "TEAM-" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 6)
        );

        return teamRepository.save(team);
    }

    public Team joinTeam(String code) {
        return teamRepository.findByTeamCode(code)
                .orElseThrow(() -> new RuntimeException("Team not found"));
    }

    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }
}