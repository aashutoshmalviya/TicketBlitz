package com.illusion.ticketblitz.notificationservice.dto;

public record PaymentResultEvent(
        String reservationId,
        String status
) {}