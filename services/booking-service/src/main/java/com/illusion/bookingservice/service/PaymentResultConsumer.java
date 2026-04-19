package com.illusion.bookingservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.illusion.bookingservice.dto.PaymentResultEvent;
import com.illusion.bookingservice.entity.Reservation;
import com.illusion.bookingservice.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentResultConsumer {

    private static final Logger log = LoggerFactory.getLogger(PaymentResultConsumer.class);
    private final ReservationRepository reservationRepository;
    private final ObjectMapper objectMapper;
    private final RedisInventoryManager inventoryManager;

    /**
     * The Main Happy-Path Listener
     */
    @KafkaListener(topics = "payment-results", groupId = "booking-group")
    public void consumePaymentResult(String eventJsonPayload) {
        try {
            PaymentResultEvent event = objectMapper.readValue(eventJsonPayload, PaymentResultEvent.class);
            log.info("Received payment result: {} for reservation: {}", event.status(), event.reservationId());

            Optional<Reservation> optionalReservation = reservationRepository.findByReservationId(event.reservationId());

            if (optionalReservation.isPresent()) {
                Reservation reservation = optionalReservation.get();

                if ("COMPLETED".equals(event.status())) {
                    reservation.setStatus("CONFIRMED");
                    log.info("Payment completed, reservation confirmed: {}", event.reservationId());
                } else {
                    reservation.setStatus("CANCELLED");
                    inventoryManager.releaseSeats(reservation.getEventId(), reservation.getQuantity());
                    log.warn("Payment failed, reservation cancelled: {}", event.reservationId());
                }

                reservationRepository.save(reservation);
            } else {
                log.error("Reservation not found for payment result: {}", event.reservationId());
            }

        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            // Bad JSON format? Unrecoverable. Route straight to DLQ.
            throw new RuntimeException("Fatal deserialization error in Booking Service", e);
        } catch (Exception e) {
            // FIXED: We MUST throw a RuntimeException here so Spring Kafka knows
            // the database save failed and triggers the 3 retries!
            log.error("Transient error processing payment result in Booking Service. Triggering retry...", e);
            throw new RuntimeException("Transient error, initiating retry...", e);
        }
    }

    /**
     * The Dead Letter Queue Listener
     * Messages arrive here ONLY if the main listener fails 4 consecutive times.
     */
    @KafkaListener(topics = "payment-results.DLT", groupId = "booking-dlq-group")
    public void consumeDeadLetterQueue(String payload,
                                       @Header(name = "kafka_dlt_exception_message", required = false) String exceptionMessage) {

        log.error("=================================================");
        log.error("☠️ CRITICAL: BOOKING SERVICE DEAD LETTER RECEIVED!");
        log.error("Payload: {}", payload);
        log.error("Reason for failure: {}", exceptionMessage);
        log.error("Action Required: Database might be out of sync with Payment Service!");
        log.error("=================================================");

        // TODO: In a production system, you would:
        // 1. Send a Slack/PagerDuty alert to the engineering team.
        // 2. Save this payload to a "booking_sync_failures" table so a cron job
        //    or admin can manually force the database update later.
    }
}