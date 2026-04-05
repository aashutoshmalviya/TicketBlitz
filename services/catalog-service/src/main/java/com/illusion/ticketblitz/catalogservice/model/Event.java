package com.illusion.ticketblitz.catalogservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
public class Event {

    @Id
    private String id;
    private String name;
    private String venue;
    private LocalDateTime eventDate;
    private BigDecimal price;

    public Event() {}

    public Event(String id, String name, String venue, LocalDateTime eventDate, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.venue = venue;
        this.eventDate = eventDate;
        this.price = price;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getVenue() { return venue; }
    public LocalDateTime getEventDate() { return eventDate; }
    public BigDecimal getPrice() { return price; }
}