package com.illusion.ticketblitz.catalogservice;

public record PaymentResultEvent(
        String reservationId,
        String status,
        String eventId) {}