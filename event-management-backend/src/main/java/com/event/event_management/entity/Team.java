package com.event.event_management.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "teams")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String teamName;

    @Column(unique = true)
    private String teamCode;

    private String leaderName;

    private Long eventId;

    @ElementCollection
    private Set<String> members = new HashSet<>();

    @CreationTimestamp
    private LocalDate createdAt;
}