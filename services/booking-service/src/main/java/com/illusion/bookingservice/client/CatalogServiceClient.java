package com.illusion.bookingservice.client;

import com.illusion.bookingservice.dto.EventDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "catalog-service", url = "http://localhost:8083")
public interface CatalogServiceClient {
    @GetMapping("/api/catalog/events/{id}/capacity")
    Integer getAvailableTickets(@PathVariable("id") String id);

    @PostMapping("/api/catalog/events/batch")
    List<EventDto> getEventsByIds(@RequestBody List<String> ids);
}