package com.illusion.bookingservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.illusion.bookingservice.dto.PaymentResultEvent;
import com.illusion.bookingservice.entity.Reservation;
import com.illusion.bookingservice.repository.ReservationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PaymentResultConsumer {

    private static final Logger log = LoggerFactory.getLogger(PaymentResultConsumer.class);
    private final ReservationRepository reservationRepository;
    private final ObjectMapper objectMapper;

    public PaymentResultConsumer(ReservationRepository reservationRepository, ObjectMapper objectMapper) {
        this.reservationRepository = reservationRepository;
        this.objectMapper = objectMapper;
    }

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
                    log.warn("Payment failed, reservation cancelled: {}", event.reservationId());
                }

                reservationRepository.save(reservation);
            } else {
                log.error("Reservation not found for payment result: {}", event.reservationId());
            }

        } catch (Exception e) {
            log.error("Failed to process payment result: {}", eventJsonPayload, e);
        }
    }
}