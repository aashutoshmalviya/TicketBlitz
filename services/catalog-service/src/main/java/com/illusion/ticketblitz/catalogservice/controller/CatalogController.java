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


    @GetMapping
    public ResponseEntity<List<Event>> getAllEvents() {
        List<Event> events = eventRepository.findAll();
        return ResponseEntity.ok(events);
    }

    @GetMapping("/{id}/capacity")
    Integer getAvailableTickets(@PathVariable("id") String id){
        Event event=eventRepository.findById(id).orElse(null);
        if(event!=null)return event.getTotalQuantity();
        else return null;
    }

    @PostMapping("/batch")
    public ResponseEntity<List<Event>> getEventsByIds(@RequestBody List<String> ids) {
        List<Event> events = eventRepository.findAllById(ids);
        return ResponseEntity.ok(events);
    }
}