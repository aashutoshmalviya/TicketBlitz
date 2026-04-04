package com.illusion.bookingservice.controller;

import com.illusion.bookingservice.service.RedisInventoryManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final RedisInventoryManager inventoryManager;

    public TicketController(RedisInventoryManager inventoryManager) {
        this.inventoryManager = inventoryManager;
    }

    @PostMapping("/reserve")
    public ResponseEntity<String> reserveTicket(@RequestParam(defaultValue = "101") String eventId) {
        boolean seatSecured = inventoryManager.reserveSeat(eventId);

        if (seatSecured) {
            // 202 Accepted: The request is valid and a seat is held.
            // The final DB save & payment will happen asynchronously later.
            return ResponseEntity.status(HttpStatus.ACCEPTED)
                    .body("Seat reserved! Proceed to payment.");
        } else {
            // 409 Conflict: The event is sold out.
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Event is sold out.");
        }
    }
}