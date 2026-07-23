package com.illusion.ticketblitz.events;

public record PaymentResultEvent(
        String reservationId,
        String status,
        String eventId,
        Integer quantity
) {
    public PaymentResultEvent(String reservationId, String status, String eventId) {
        this(reservationId, status, eventId, 1);
    }

    public PaymentResultEvent(String reservationId, String status) {
        this(reservationId, status, null, 1);
    }
}
