package com.illusion.bookingservice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.bouncycastle.asn1.x500.style.RFC4519Style.description;

public record EventDto(
        String id,
        String name,
        String venue,
        LocalDateTime eventDate,
        BigDecimal price,
        Integer totalQuantity
) {}