package com.illusion.ticketblitz.notificationservice.services;

import tools.jackson.databind.ObjectMapper;
import com.illusion.ticketblitz.notificationservice.dto.PaymentResultEvent;
import com.illusion.ticketblitz.notificationservice.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationConsumer {

    private static final Logger log = LoggerFactory.getLogger(NotificationConsumer.class);
    private final ObjectMapper objectMapper;
    private final EmailService emailService;

    public NotificationConsumer(ObjectMapper objectMapper, EmailService emailService) {
        this.objectMapper = objectMapper;
        this.emailService = emailService;
    }

    @KafkaListener(topics = "payment-results", groupId = "notification-group")
    public void consumePaymentResult(String eventJsonPayload) {
        try {
            PaymentResultEvent event = objectMapper.readValue(eventJsonPayload, PaymentResultEvent.class);
            log.info("Received payment result: {} for reservation: {}", event.status(), event.reservationId());

            if ("COMPLETED".equals(event.status())) {
                log.info("Payment completed, sending ticket email for reservation: {}", event.reservationId());
                emailService.sendTicketEmail(event.reservationId());
            } else {
                log.warn("Payment failed for reservation: {}, skipping email", event.reservationId());
            }

        } catch (Exception e) {
            log.error("Failed to process payment result notification", e);
        }
    }
}