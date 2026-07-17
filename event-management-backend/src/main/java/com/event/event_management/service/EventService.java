package com.event.event_management.service;

import com.event.event_management.entity.Event;
import com.event.event_management.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

import java.util.List;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @CacheEvict(value = "events", allEntries = true)
    public Event createEvent(Event event) {

        return eventRepository.save(event);
    }

    @Cacheable("events")
    public List<Event> getAllEvents() {
        System.out.println("Fetching Events from Database...");
        return eventRepository.findAll();
    }

    @CacheEvict(value = "events", allEntries = true)
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

    @CacheEvict(value = "events", allEntries = true)
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