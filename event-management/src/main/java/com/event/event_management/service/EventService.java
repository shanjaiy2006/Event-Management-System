package com.event.event_management.service;

import com.event.event_management.entity.Event;
import com.event.event_management.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    public Event createEvent(Event event) {
        return eventRepository.save(event);
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Event updateEvent(
            Long id,
            Event updatedEvent) {

        Event event = eventRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Event not found"));

        event.setTitle(updatedEvent.getTitle());
        event.setDescription(updatedEvent.getDescription());
        event.setVenue(updatedEvent.getVenue());
        event.setEventDate(updatedEvent.getEventDate());

        event.setCreatedBy(updatedEvent.getCreatedBy());
        event.setMaxCapacity(updatedEvent.getMaxCapacity());
        event.setRegistrationDeadline(
                updatedEvent.getRegistrationDeadline()
        );

        return eventRepository.save(event);
    }

    public void deleteEvent(Long id) {

        Event event = eventRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Event not found"));

        eventRepository.delete(event);
    }

    public Event getEventById(Long id) {

        return eventRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Event not found"));
    }

}