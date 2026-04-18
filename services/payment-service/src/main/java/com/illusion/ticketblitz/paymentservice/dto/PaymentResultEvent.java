package com.illusion.ticketblitz.paymentservice.dto;

public record PaymentResultEvent(
        String reservationId,
        String status,
        String eventId) {}