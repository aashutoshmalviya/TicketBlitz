package com.illusion.ticketblitz.catalogservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.illusion.ticketblitz.catalogservice.PaymentResultEvent;
import com.illusion.ticketblitz.catalogservice.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentResultConsumer {

    private static final Logger log = LoggerFactory.getLogger(PaymentResultConsumer.class);
    private final EventRepository eventRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    @KafkaListener(topics = "payment-results", groupId = "catalog-group")
    public void consumePaymentResult(String eventJsonPayload) {
        try {
            PaymentResultEvent event = objectMapper.readValue(eventJsonPayload, PaymentResultEvent.class);
            log.info("Received payment result: {} for reservation: {}", event.status(), event.reservationId());

            if ("COMPLETED".equals(event.status())) {
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

        } catch (Exception e) {
            log.error("Failed to process payment result: {}", eventJsonPayload, e);
        }
    }
}