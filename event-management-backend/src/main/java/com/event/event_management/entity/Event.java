package com.event.event_management.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;


@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    private String venue;

    private LocalDate eventDate;

    private String createdBy;

    private Integer maxCapacity;

    private LocalDate registrationDeadline;
}