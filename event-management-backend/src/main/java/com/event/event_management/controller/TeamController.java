package com.event.event_management.controller;

import com.event.event_management.entity.Team;
import com.event.event_management.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/teams")
public class TeamController {

    @Autowired
    private TeamService teamService;

    @PostMapping
    public Team createTeam(@RequestBody Team team) {
        String email = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        return teamService.createTeam(team, email);
    }

    @Autowired
    private com.event.event_management.repository.UserRepository userRepository;

    @GetMapping
    public List<Team> getAllTeams() {
        String role = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getAuthorities().iterator().next().getAuthority();
        String email = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        
        if (role.contains("ADMIN")) {
            return teamService.getAllTeams();
        } else {
            return teamService.getTeamsByMemberEmail(email);
        }
    }

    @GetMapping("/join/{code}")
    public Team joinTeam(@PathVariable String code) {
        String email = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        return teamService.joinTeam(code, email);
    }
}