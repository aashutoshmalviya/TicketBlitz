package com.illusion.ticketblitz.catalogservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Event {
    @Id
    private String id;
    private String name;
    private String venue;
    private LocalDateTime eventDate;
    private BigDecimal price;
    private Integer totalQuantity;

}