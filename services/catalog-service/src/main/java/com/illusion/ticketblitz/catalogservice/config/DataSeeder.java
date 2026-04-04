package com.illusion.ticketblitz.catalogservice.config;

import com.illusion.ticketblitz.catalogservice.model.Event;
import com.illusion.ticketblitz.catalogservice.repository.EventRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class DataSeeder implements CommandLineRunner {

    private final EventRepository repository;

    public DataSeeder(EventRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        if (repository.count() == 0) {
            repository.save(new Event(
                    "101",
                    "The Eras Tour",
                    "Wembley Stadium",
                    LocalDateTime.now().plusMonths(2),
                    new BigDecimal("150.00")
            ));
            System.out.println("Database seeded with Event 101!");
        }
    }
}