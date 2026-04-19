package com.illusion.bookingservice.controller;

import com.illusion.bookingservice.client.CatalogServiceClient;
import com.illusion.bookingservice.dto.BookingResponse;
import com.illusion.bookingservice.dto.EventDto;
import com.illusion.bookingservice.entity.Reservation;
import com.illusion.bookingservice.repository.ReservationRepository;
import com.illusion.bookingservice.service.ReservationEventProducer;
import com.illusion.bookingservice.service.RedisInventoryManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {
    private static final Logger log = LoggerFactory.getLogger(TicketController.class);
    private final RedisInventoryManager inventoryManager;
    private final ReservationEventProducer eventProducer;
    private final CatalogServiceClient catalogClient;
    private final ReservationRepository reservationRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy, hh:mm a");

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


    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookingResponse>> getUserBookings(@PathVariable String userId) {
        List<Reservation> reservations = reservationRepository.findByUserId(userId);
        if (reservations.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        Set<String> uniqueEventIds = reservations.stream()
                .map(Reservation::getEventId)
                .filter(id -> id != null && !id.trim().isEmpty())
                .collect(Collectors.toSet());
        Map<String, EventDto> eventMap = new HashMap<>();
        try {
            if (!uniqueEventIds.isEmpty()) {
                List<EventDto> batchEvents = catalogClient.getEventsByIds(new ArrayList<>(uniqueEventIds));
                batchEvents.forEach(event -> eventMap.put(event.id(), event));
            }
        } catch (Exception e) {
            log.warn("Batch fetch from Catalog Service failed! Users will see 'Unknown Event'.", e);
        }
        List<BookingResponse> enrichedBookings = reservations.stream().map(res -> {
            EventDto eventDetails = eventMap.get(res.getEventId());

            String eventName = eventDetails != null ? eventDetails.name() : "Unknown Event";
            // Put this at the top of your TicketController class (so it's only created once)



            String eventDate = (eventDetails != null && eventDetails.eventDate() != null)
                    ? eventDetails.eventDate().format(DATE_FORMATTER)
                    : "TBD";

            return new BookingResponse(
                    res.getReservationId(),
                    res.getEventId(),
                    eventName,
                    eventDate,
                    res.getQuantity(),
                    res.getStatus()
            );
        }).toList();

        return ResponseEntity.ok(enrichedBookings);
    }
}