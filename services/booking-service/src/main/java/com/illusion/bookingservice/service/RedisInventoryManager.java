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

        // Atomic DECR prevents race conditions during high concurrency
        Long remainingSeats = redisTemplate.opsForValue().decrement(key);

        if (remainingSeats != null && remainingSeats >= 0) {
            return true; // Seat secured
        } else {
            // Oversold - rollback to prevent negative inventory
            redisTemplate.opsForValue().increment(key);
            return false;
        }
    }
}