package com.illusion.ticketblitz.paymentservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import tools.jackson.databind.ObjectMapper;
import com.illusion.ticketblitz.paymentservice.dto.PaymentResultEvent;
import com.illusion.ticketblitz.paymentservice.dto.ReservationCreatedEvent;
import com.illusion.ticketblitz.paymentservice.entity.PaymentRecord;
import com.illusion.ticketblitz.paymentservice.gateway.PaymentProvider;
import com.illusion.ticketblitz.paymentservice.gateway.PaymentResult;
import com.illusion.ticketblitz.paymentservice.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class ReservationEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(ReservationEventConsumer.class);

    private final PaymentRepository paymentRepository;
    private final PaymentProvider paymentProvider;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public ReservationEventConsumer(PaymentRepository paymentRepository,
                                    PaymentProvider paymentProvider,
                                    ObjectMapper objectMapper,
                                    KafkaTemplate<String, String> kafkaTemplate) {
        this.paymentRepository = paymentRepository;
        this.paymentProvider = paymentProvider;
        this.objectMapper = objectMapper;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "order-events", groupId = "payment-group")
    @Transactional
    public void consumeReservationEvent(String eventJsonPayload) {
        try {
            ReservationCreatedEvent event = objectMapper.readValue(eventJsonPayload, ReservationCreatedEvent.class);
            log.info("Received reservation event: {}", event.reservationId());

            BigDecimal totalAmount = BigDecimal.valueOf(50L * event.quantity());

            PaymentRecord payment = PaymentRecord.builder()
                    .reservationId(event.reservationId())
                    .userId(event.userId())
                    .amount(totalAmount)
                    .currency("USD")
                    .status("PROCESSING")
                    .build();
            paymentRepository.save(payment);

            PaymentResult result = paymentProvider.processPayment(event.userId(), totalAmount, "USD");

            if (result.success()) {
                payment.setStatus("COMPLETED");
                payment.setExternalTransactionId(result.transactionId());
                log.info("Payment completed for reservation: {}", event.reservationId());
            } else {
                payment.setStatus("FAILED");
                log.error("Payment failed for reservation: {} - {}", event.reservationId(), result.errorMessage());
            }
            paymentRepository.save(payment);
            log.info("status of payment ", payment.getStatus());
            // Publish payment result to trigger downstream processing
            PaymentResultEvent resultEvent = new PaymentResultEvent(event.reservationId(), payment.getStatus(),event.eventId());
            String resultJson = objectMapper.writeValueAsString(resultEvent);
            kafkaTemplate.send("payment-results", event.reservationId(), resultJson);
            log.info("Published payment result event for reservation: {}", event.reservationId());

        } catch (Exception e) {
            log.error("Failed to process reservation event", e);
        }
    }
}