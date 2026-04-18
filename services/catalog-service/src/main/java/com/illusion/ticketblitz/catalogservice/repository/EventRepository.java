package com.illusion.ticketblitz.catalogservice.repository;

import com.illusion.ticketblitz.catalogservice.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, String> {

    @Modifying
    @Query("UPDATE Event e SET e.totalQuantity = e.totalQuantity - :total_quantity " +
            "WHERE e.id = :id AND e.totalQuantity >= :total_quantity")
    int decrementInventorySafely(@Param("id") String id, @Param("total_quantity") int total_quantity);
}