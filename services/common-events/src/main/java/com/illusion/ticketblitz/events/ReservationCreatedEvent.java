package com.illusion.ticketblitz.events;

public record ReservationCreatedEvent(
        String reservationId,
        String eventId,
        String userId,
        Integer quantity
) {}
