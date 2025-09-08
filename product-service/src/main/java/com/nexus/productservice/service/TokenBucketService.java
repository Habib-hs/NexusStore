package com.nexus.productservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class TokenBucketService {
    private final RedisTemplate<String, String> redisTemplate;
    private static final int MAX_TOKENS = 5;
    private static final int REFILL_INTERVAL = 60; // seconds

    public TokenBucketService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean tryConsumeToken(String identifier) {
        String key = "ratelimit:" + identifier;

        if (Boolean.FALSE.equals(redisTemplate.hasKey(key))) {
            redisTemplate.opsForValue().set(key, String.valueOf(MAX_TOKENS - 1), REFILL_INTERVAL, TimeUnit.SECONDS);
            return true;
        }

        String tokenValue = redisTemplate.opsForValue().get(key);
        if (tokenValue == null) {
            return false;
        }

        int tokens = Integer.parseInt(tokenValue);
        if (tokens > 0) {
            redisTemplate.opsForValue().decrement(key);
            return true;
        } else {
            log.error("Rate limit exceeded for identifier: {}", identifier);
            return false;
        }
    }
}
