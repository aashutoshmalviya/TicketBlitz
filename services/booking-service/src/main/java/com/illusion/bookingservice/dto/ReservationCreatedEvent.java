package com.illusion.bookingservice.dto;

public record ReservationCreatedEvent(
        String reservationId,
        String eventId,
        String userId,
        Integer quantity
) {}