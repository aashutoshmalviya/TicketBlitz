package com.illusion.bookingservice.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisInventoryManager {

    private final StringRedisTemplate redisTemplate;

    public RedisInventoryManager(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean reserveSeat(String eventId) {
        String key = "inventory:event:" + eventId;

        // Atomically decrement the inventory
        Long remainingSeats = redisTemplate.opsForValue().decrement(key);

        if (remainingSeats != null && remainingSeats >= 0) {
            // Success: User secured a ticket in the cache
            return true;
        } else {
            // Sold Out: Increment it back up to prevent negative numbers
            redisTemplate.opsForValue().increment(key);
            return false;
        }
    }
}