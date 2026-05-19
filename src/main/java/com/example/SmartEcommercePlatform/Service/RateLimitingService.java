package com.example.SmartEcommercePlatform.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RateLimitingService {

    private final StringRedisTemplate redisTemplate;
    private static final int MAX_REQUESTS_PER_MINUTE = 10;
    private static final long WINDOW_SIZE_IN_MS = 60000; // 1 Minute

    public boolean isRequestAllowed(String ipAddress) {
        String redisKey = "rate_limit:ip:" + ipAddress;
        long currentTime = System.currentTimeMillis();
        long windowStart = currentTime - WINDOW_SIZE_IN_MS;

        try {
            redisTemplate.opsForZSet().removeRangeByScore(redisKey, 0, windowStart);
            Long currentRequestCount = redisTemplate.opsForZSet().zCard(redisKey);
            if (currentRequestCount != null && currentRequestCount >= MAX_REQUESTS_PER_MINUTE) {
                log.warn("Rate limit exceeded for IP: {}", ipAddress);
                return false;
            }
            String uniqueRequestValue = currentTime + "-" + UUID.randomUUID();
            redisTemplate.opsForZSet().add(redisKey, uniqueRequestValue, currentTime);
            redisTemplate.expire(redisKey, Duration.ofMinutes(2));

            return true;

        } catch (Exception e) {
            log.error("Redis Rate Limiter failed, allowing request to prevent outage: {}", e.getMessage());
            return true;
        }
    }
}