package com.illusion.ticketblitz.catalogservice.controller;

import com.illusion.ticketblitz.catalogservice.model.Event;
import com.illusion.ticketblitz.catalogservice.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalog/events") // Consolidated mapping here
@RequiredArgsConstructor // Automatically generates the constructor for eventRepository
public class CatalogController {

    private final EventRepository eventRepository;

    /**
     * 1. Fetch All Events
     * Used by the Angular frontend to display the main Event Catalog page.
     */
    @GetMapping
    public ResponseEntity<List<Event>> getAllEvents() {
        List<Event> events = eventRepository.findAll();
        return ResponseEntity.ok(events);
    }

    /**
     * 2. Fetch Single Event by ID
     * THE MISSING ENDPOINT! Used by the frontend Event Details page AND internal Feign Clients.
     */
    @GetMapping("/{id}/event")
    public ResponseEntity<Event> getEventById(@PathVariable("id") String id) {
        return eventRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    /**
     * 3. Fetch Multiple Events in Batch
     * Solves the N+1 problem for the Booking Service Dashboard!
     */
    @PostMapping("/batch")
    public ResponseEntity<List<Event>> getEventsByIds(@RequestBody List<String> ids) {
        List<Event> events = eventRepository.findAllById(ids);
        return ResponseEntity.ok(events);
    }
}