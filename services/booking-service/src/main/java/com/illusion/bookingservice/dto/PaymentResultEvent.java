package com.illusion.bookingservice.dto;

public record PaymentResultEvent(
        String reservationId,
        String status
) {}