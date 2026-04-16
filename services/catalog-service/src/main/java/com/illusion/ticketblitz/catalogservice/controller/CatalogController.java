package com.illusion.ticketblitz.catalogservice.controller;

import com.illusion.ticketblitz.catalogservice.model.Event;
import com.illusion.ticketblitz.catalogservice.repository.EventRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/catalog")
public class CatalogController {

    private final EventRepository eventRepository;

    public CatalogController(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @GetMapping("/events")
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @GetMapping("/events/{id}/capacity")
    Integer getAvailableTickets(@PathVariable("id") String id){
        Event event=eventRepository.findById(id).orElse(null);
        if(event!=null)return event.getTotalQuantity();
        else return null;
    }
}