package com.illusion.bookingservice.dto;

public record BookingResponse(
        String reservationId,
        String eventId,
        String eventName,
        String eventDate,
        Integer quantity,
        String status
) {}