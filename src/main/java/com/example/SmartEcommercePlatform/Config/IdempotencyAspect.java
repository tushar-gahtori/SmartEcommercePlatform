package com.example.SmartEcommercePlatform.Config;

import com.example.SmartEcommercePlatform.Exception.BadRequestException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Duration;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class IdempotencyAspect {

    private final StringRedisTemplate redisTemplate;

    @Around("@annotation(com.example.SmartEcommercePlatform.Security.Idempotent)")
    public Object checkIdempotency(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String idempotencyKey = request.getHeader("Idempotency-Key");
        if (idempotencyKey == null || idempotencyKey.trim().isEmpty()) {
            throw new BadRequestException("Idempotency-Key header is required for this operation.");
        }
        String redisKey = "idempotency:" + idempotencyKey;
        Boolean isNewRequest = redisTemplate.opsForValue().setIfAbsent(redisKey, "PROCESSING", Duration.ofMinutes(5));
        if (Boolean.FALSE.equals(isNewRequest)) {
            log.warn("Duplicate request intercepted for Idempotency Key: {}", idempotencyKey);
            throw new BadRequestException("Duplicate request detected. Your order is already being processed.");
        }
        return joinPoint.proceed();
    }
}