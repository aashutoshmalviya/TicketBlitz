package com.illusion.ticketblitz.catalogservice.repository;

import com.illusion.ticketblitz.catalogservice.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, String> {
}