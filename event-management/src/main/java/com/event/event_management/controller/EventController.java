package com.event.event_management.controller;

import com.event.event_management.entity.Event;
import com.event.event_management.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
@Tag(name = "Event APIs")
@RestController
@RequestMapping("/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @Operation(summary = "Create Event")
    @PostMapping
    public Event createEvent(@RequestBody Event event) {
        return eventService.createEvent(event);
    }

    @Operation(summary = "Get All Events")
    @GetMapping
    public List<Event> getAllEvents() {
        return eventService.getAllEvents();
    }

    @Operation(summary = "Update Event")
    @PutMapping("/{id}")
    public Event updateEvent(
            @PathVariable Long id,
            @RequestBody Event updatedEvent) {

        return eventService.updateEvent(id, updatedEvent);
    }

    @Operation(summary = "Delete Event")
    @DeleteMapping("/{id}")
    public String deleteEvent(
            @PathVariable Long id) {

        eventService.deleteEvent(id);

        return "Event Deleted Successfully";
    }

    @GetMapping("/{id}")
    public Event getEventById(
            @PathVariable Long id) {

        return eventService.getEventById(id);
    }


}