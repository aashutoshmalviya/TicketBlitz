package com.illusion.bookingservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.illusion.bookingservice.dto.ReservationCreatedEvent;
import com.illusion.bookingservice.entity.Reservation;
import com.illusion.bookingservice.repository.ReservationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ReservationEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(ReservationEventConsumer.class);
    private final ReservationRepository reservationRepository;
    private final ObjectMapper objectMapper;

    public ReservationEventConsumer(ReservationRepository reservationRepository, ObjectMapper objectMapper) {
        this.reservationRepository = reservationRepository;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "order-events", groupId = "booking-group")
    public void consumeReservationEvent(String eventJsonPayload) {
        try {
            ReservationCreatedEvent event = objectMapper.readValue(eventJsonPayload, ReservationCreatedEvent.class);

            log.info("Consumed reservation event: {} for event: {}", event.reservationId(), event.eventId());

            Reservation reservation = Reservation.builder()
                    .reservationId(event.reservationId())
                    .eventId(event.eventId())
                    .userId(event.userId())
                    .quantity(event.quantity())
                    .status("PENDING_PAYMENT")
                    .createdAt(LocalDateTime.now())
                    .build();

            reservationRepository.save(reservation);

            log.info("Reservation persisted to database: {}", event.reservationId());

        } catch (Exception e) {
            log.error("Failed to process reservation event: {}", eventJsonPayload, e);
        }
    }
}