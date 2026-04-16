package com.illusion.bookingservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "catalog-service", url = "http://localhost:8083")
public interface CatalogServiceClient {
    @GetMapping("/api/catalog/events/{id}/capacity")
    Integer getAvailableTickets(@PathVariable("id") String id);
}