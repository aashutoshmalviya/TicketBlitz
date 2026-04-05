package com.illusion.bookingservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.illusion.bookingservice.dto.ReservationCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ReservationEventProducer {

    private static final Logger log = LoggerFactory.getLogger(ReservationEventProducer.class);
    private static final String TOPIC = "order-events";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public ReservationEventProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void publishReservationEvent(String eventId, String userId, Integer quantity) {
        try {
            String reservationId = UUID.randomUUID().toString();
            ReservationCreatedEvent event = new ReservationCreatedEvent(reservationId, eventId, userId, quantity);
            String jsonPayload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(TOPIC, reservationId, jsonPayload);
            log.info("Published reservation event: {} for user: {}", reservationId, userId);
        } catch (Exception e) {
            log.error("Failed to publish reservation event", e);
        }
    }
}
