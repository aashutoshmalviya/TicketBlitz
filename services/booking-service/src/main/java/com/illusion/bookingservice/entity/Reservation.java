package com.illusion.bookingservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Reservation {
    @Id
    private String reservationId; // Kafka event correlation ID
    private String eventId;
    private String userId;
    private Integer quantity;
    private String status;
    private LocalDateTime createdAt;
}