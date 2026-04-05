package com.illusion.bookingservice.controller;

import com.illusion.bookingservice.service.ReservationEventProducer;
import com.illusion.bookingservice.service.RedisInventoryManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final RedisInventoryManager inventoryManager;
    private final ReservationEventProducer eventProducer;


    public TicketController(RedisInventoryManager inventoryManager, ReservationEventProducer eventProducer) {
        this.inventoryManager = inventoryManager;
        this.eventProducer = eventProducer;
    }


    public record ReservationRequest(String eventId, String userId, Integer quantity) {}

    @PostMapping("/reserve")
    public ResponseEntity<String> reserveTicket(@RequestBody ReservationRequest request) {

        boolean seatSecured = inventoryManager.reserveSeat(request.eventId());

        if (seatSecured) {
            eventProducer.publishReservationEvent(
                    request.eventId(),
                    request.userId(),
                    request.quantity()
            );

            return ResponseEntity.status(HttpStatus.ACCEPTED)
                    .body("Ticket reserved! Processing payment...");
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Event is sold out.");
        }
    }
}