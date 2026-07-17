package com.event.event_management.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "registrations",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"studentEmail", "eventId"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Registration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String studentName;

    private String studentEmail;

    private Long eventId;
}