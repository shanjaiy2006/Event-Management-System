package com.event.event_management.repository;

import com.event.event_management.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {

    Optional<Team> findByTeamCode(String teamCode);
    
    java.util.List<Team> findByLeaderName(String leaderName);

    java.util.List<Team> findByMembersContaining(String email);

}