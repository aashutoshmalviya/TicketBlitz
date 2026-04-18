package com.illusion.bookingservice.service;

import com.illusion.bookingservice.client.CatalogServiceClient;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;
import java.util.Collections;

import java.time.Duration;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


@Service
public class RedisInventoryManager {

    private final RedisTemplate<String, String> redisTemplate;
    private final RedisScript<Long> decrIfPositiveScript;
    private static final Logger log = LoggerFactory.getLogger(RedisInventoryManager.class);
    private final Lock cacheLock = new ReentrantLock();
    private final CatalogServiceClient catalogClient;
    public RedisInventoryManager(RedisTemplate<String, String> redisTemplate, CatalogServiceClient catalogClient) {
        this.redisTemplate = redisTemplate;
        this.catalogClient = catalogClient;

        this.decrIfPositiveScript = RedisScript.of(
                "local current = redis.call('GET', KEYS[1]) " +
                        "if not current then " +
                        "  return -1 " + //Cache missed check key doesnt exists
                        "end " +
                        "if tonumber(current) > 0 then " +
                        "  redis.call('DECR', KEYS[1]) " +
                        "  return 1 " +  // SUCCESS
                        "end " +
                        "return 0 ", //Sold out
                Long.class
        );

    }

    public boolean reserveSeat(String eventId) {
        String key = "inventory:event:" + eventId;

        Long result = redisTemplate.execute(
            decrIfPositiveScript,
            Collections.singletonList(key)
        );
        // Cache Hit & Ticket Secured
        if (result != null && result == 1L) {
            return true;
        }

        // Cache Miss(Failsafe Engaged)
        if (result != null && result == -1L) {
            log.warn("Cache Miss for event {}. Engaging Failsafe Lazy Load...", eventId);
            return handleCacheMissAndRetry(eventId, key);
        }

        // Sold out
        return false;
    }
    private boolean handleCacheMissAndRetry(String eventId, String redisKey) {
        // Lock thread
        cacheLock.lock();
        try {
            // Double-check if another thread already fixed the cache while we were waiting
            if (Boolean.TRUE.equals(redisTemplate.hasKey(redisKey))) {
                return reserveSeat(eventId); // Retry fast-path
            }

            // Fetch the real available capacity
            log.info("Fetching true inventory from Postgres for event {}", eventId);
            Integer availableTickets = catalogClient.getAvailableTickets(eventId);


            if (availableTickets == null) {
                log.error("Event {} not found in Catalog Service!", eventId);
                return false;
            }

            // Write it to Redis with a TTL of 24 hours
            redisTemplate.opsForValue().set(redisKey, String.valueOf(availableTickets), Duration.ofHours(24));

            // Retry
            return reserveSeat(eventId);

        } finally {
            // Unlock
            cacheLock.unlock();
        }
    }
    /**
     * Compensating transaction to release seats back into Redis if payment fails.
     */
    public void releaseSeats(String eventId, int quantity) {
        String key = "inventory:event:" + eventId;

        // Increment the inventory back up
        redisTemplate.opsForValue().increment(key, quantity);
        log.info("Released {} seats back to Redis for event {}", quantity, eventId);
    }
}