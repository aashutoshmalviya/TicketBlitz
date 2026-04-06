package com.illusion.bookingservice.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;
import java.util.Collections;

@Service
public class RedisInventoryManager {

    private final RedisTemplate<String, String> redisTemplate;
    private final RedisScript<Long> decrIfPositiveScript;

    public RedisInventoryManager(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        
        this.decrIfPositiveScript = RedisScript.of(
            "if redis.call('GET', KEYS[1]) then " +
            "  local current = tonumber(redis.call('GET', KEYS[1])) " +
            "  if current > 0 then " +
            "    redis.call('DECR', KEYS[1]) " +
            "    return 1 " +
            "  end " +
            "end " +
            "return 0",
            Long.class
        );
    }

    public boolean reserveSeat(String eventId) {
        String key = "inventory:event:" + eventId;

        Long result = redisTemplate.execute(
            decrIfPositiveScript,
            Collections.singletonList(key)
        );

        return result != null && result == 1L;
    }
}