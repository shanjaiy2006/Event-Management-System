package com.event.event_management.controller;

import com.event.event_management.entity.Event;
import com.event.event_management.service.EventService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Tag(name = "Event APIs")
@RestController
@RequestMapping("/events")
@SecurityRequirement(name = "bearerAuth")
public class EventController {

    @Autowired
    private EventService eventService;

    private static final Logger logger =
            LoggerFactory.getLogger(EventController.class);


    @Operation(
            summary = "Create Event",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping
    public Event createEvent(@RequestBody Event event) {
        logger.info("Creating event {}", event.getTitle());
        return eventService.createEvent(event);
    }

    @Operation(summary = "Get All Events")
    @GetMapping
    public List<Event> getAllEvents() {
        return eventService.getAllEvents();
    }

    @Operation(summary = "Update Event",
            security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/{id}")
    public Event updateEvent(
            @PathVariable Long id,
            @RequestBody Event updatedEvent) {

        return eventService.updateEvent(id, updatedEvent);
    }

    @Operation(summary = "Delete Event",
            security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/{id}")
    public String deleteEvent(@PathVariable Long id) {

        eventService.deleteEvent(id);
        return "Event Deleted Successfully";
    }

    @Operation(summary = "Get Event By Id")
    @GetMapping("/{id}")
    public Event getEventById(@PathVariable Long id) {

        return eventService.getEventById(id);
    }
}