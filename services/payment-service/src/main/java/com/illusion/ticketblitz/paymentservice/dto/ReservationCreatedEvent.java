package com.illusion.ticketblitz.paymentservice.dto;

public record ReservationCreatedEvent(
        String reservationId,
        String eventId,
        String userId,
        Integer quantity
) {}