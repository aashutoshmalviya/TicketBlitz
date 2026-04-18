package com.illusion.bookingservice.controller;

import com.illusion.bookingservice.entity.Reservation;
import com.illusion.bookingservice.repository.ReservationRepository;
import com.illusion.bookingservice.service.ReservationEventProducer;
import com.illusion.bookingservice.service.RedisInventoryManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@RestController
@RequestMapping("/api/tickets")
public class TicketController {
    private static final Logger log = LoggerFactory.getLogger(TicketController.class);
    private final RedisInventoryManager inventoryManager;
    private final ReservationEventProducer eventProducer;

    private final ReservationRepository reservationRepository;

    public TicketController(RedisInventoryManager inventoryManager,
                            ReservationEventProducer eventProducer,
                            ReservationRepository reservationRepository) {
        this.inventoryManager = inventoryManager;
        this.eventProducer = eventProducer;
        this.reservationRepository = reservationRepository;
    }

    public record ReservationRequest(String eventId, String userId, Integer quantity) {}

    @PostMapping("/reserve")

    public ResponseEntity<Map<String, String>> reserveTicket(@RequestBody ReservationRequest request) {


        String reservationId = UUID.randomUUID().toString();


        boolean seatSecured = inventoryManager.reserveSeat(request.eventId());

        if (seatSecured) {

            Reservation reservation = new Reservation();
            reservation.setReservationId(reservationId);
            reservation.setEventId(request.eventId());
            reservation.setUserId(request.userId());
            reservation.setQuantity(request.quantity());
            reservation.setStatus("PROCESSING");
            reservationRepository.save(reservation);

            // 4. Fire the event to Kafka (including the reservationId!)
            eventProducer.publishReservationEvent(
                    reservationId,
                    request.eventId(),
                    request.userId(),
                    request.quantity()
            );
            Map<String, String> response = new HashMap<>();
            response.put("reservationId", reservationId);
            response.put("message", "Ticket reserved! Processing payment...");
            response.put("status", "ACCEPTED");

            return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
        } else {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Event is sold out or insufficient tickets.");
            errorResponse.put("status", "FAILED");

            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }
    }

    @GetMapping("/status/{reservationId}")
    public ResponseEntity<Map<String, String>> getReservationStatus(@PathVariable String reservationId) {

        Optional<Reservation> reservationOpt = reservationRepository.findByReservationId(reservationId);
        Map<String, String> response = new HashMap<>();
        log.info("Payment status",reservationOpt.get().getStatus());
        if (reservationOpt.isPresent()) {
            response.put("status", reservationOpt.get().getStatus());
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "FAILED");
            response.put("error", "Reservation not found");

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}