package com.illusion.ticketblitz.catalogservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.illusion.ticketblitz.catalogservice.PaymentResultEvent;
import com.illusion.ticketblitz.catalogservice.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentResultConsumer {

    private static final Logger log = LoggerFactory.getLogger(PaymentResultConsumer.class);
    private final EventRepository eventRepository;
    private final ObjectMapper objectMapper;

    /**
     * The Main Happy-Path Listener
     */
    @Transactional
    @KafkaListener(topics = "payment-results", groupId = "catalog-group")
    public void consumePaymentResult(String eventJsonPayload) {
        try {
            PaymentResultEvent event = objectMapper.readValue(eventJsonPayload, PaymentResultEvent.class);
            log.info("Received payment result: {} for reservation: {}", event.status(), event.reservationId());

            if ("COMPLETED".equals(event.status())) {

                // FIXED: Use event.quantity() instead of hardcoded 1!
                int rowsUpdated = eventRepository.decrementInventorySafely(
                        event.eventId(),
                       1
                );

                if (rowsUpdated > 0) {
                    log.info("Successfully deducted {} tickets for event {}", 1, event.eventId());
                } else {
                    log.error("CRITICAL: Failed to deduct inventory! Event {} might be sold out or invalid.", event.eventId());
                }
            } else {
                log.info("Payment was not COMPLETED. Status: {}. Ignoring inventory deduction.", event.status());
            }

        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            // Bad JSON? Unrecoverable. Route straight to DLQ.
            throw new RuntimeException("Fatal deserialization error in Catalog Service", e);
        } catch (Exception e) {
            // FIXED: We MUST throw a RuntimeException here to trigger the 3 retries!
            log.error("Transient error deducting inventory. Triggering retry...", e);
            throw new RuntimeException("Transient error, initiating retry...", e);
        }
    }

    /**
     * The Dead Letter Queue Listener
     * Messages arrive here ONLY if the main listener fails 4 consecutive times.
     */
    @KafkaListener(topics = "payment-results.DLT", groupId = "catalog-dlq-group")
    public void consumeDeadLetterQueue(String payload,
                                       @Header(name = "kafka_dlt_exception_message", required = false) String exceptionMessage) {

        log.error("=================================================");
        log.error("☠️ CRITICAL: CATALOG SERVICE DEAD LETTER RECEIVED!");
        log.error("Payload: {}", payload);
        log.error("Reason for failure: {}", exceptionMessage);
        log.error("Action Required: Manual inventory deduction may be required to prevent overselling!");
        log.error("=================================================");

        // TODO: Future task
        // Save this failed deduction to a database table. A background job can periodically
        // attempt to replay these deductions to ensure the Catalog matches the Booking DB.
    }
}