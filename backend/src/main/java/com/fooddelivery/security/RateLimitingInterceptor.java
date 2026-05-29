package com.fooddelivery.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooddelivery.dto.ErrorResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Lightweight, production-safe rate limiting interceptor.
 * Protects sensitive endpoints (Auth, Checkout, Payments, Logistics) from brute-force and abuse.
 * Uses an in-memory token bucket approach per IP address.
 */
@Component
public class RateLimitingInterceptor implements HandlerInterceptor {

    private final Map<String, TokenBucket> buckets = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;

    // Configuration: 10 requests per minute for sensitive endpoints
    private static final int CAPACITY = 10;
    private static final long REFILL_PERIOD_MS = TimeUnit.MINUTES.toMillis(1);

    public RateLimitingInterceptor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();
        
        // Only apply throttling to sensitive endpoints
        if (isSensitivePath(path)) {
            String clientIp = getClientIp(request);
            TokenBucket bucket = buckets.computeIfAbsent(clientIp, k -> new TokenBucket(CAPACITY, REFILL_PERIOD_MS));

            if (!bucket.tryConsume()) {
                sendErrorResponse(request, response);
                return false;
            }
        }
        
        return true;
    }

    private boolean isSensitivePath(String path) {
        return path.startsWith("/api/auth/login") ||
               path.startsWith("/api/auth/register") ||
               path.startsWith("/api/orders/checkout") ||
               path.startsWith("/api/payments/verify") ||
               path.startsWith("/api/delivery/assign");
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private void sendErrorResponse(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType("application/json");

        ErrorResponseDto error = new ErrorResponseDto(
                LocalDateTime.now(),
                HttpStatus.TOO_MANY_REQUESTS.value(),
                "Too Many Requests",
                "Rate limit exceeded. Please try again in a minute.",
                request.getRequestURI()
        );

        objectMapper.writeValue(response.getOutputStream(), error);
    }

    /**
     * Simple thread-safe Token Bucket implementation.
     */
    private static class TokenBucket {
        private final int capacity;
        private final long refillPeriodMs;
        private double tokens;
        private long lastRefillTime;

        public TokenBucket(int capacity, long refillPeriodMs) {
            this.capacity = capacity;
            this.refillPeriodMs = refillPeriodMs;
            this.tokens = capacity;
            this.lastRefillTime = System.currentTimeMillis();
        }

        public synchronized boolean tryConsume() {
            refill();
            if (tokens >= 1) {
                tokens -= 1;
                return true;
            }
            return false;
        }

        private void refill() {
            long now = System.currentTimeMillis();
            long timePassed = now - lastRefillTime;
            double tokensToAdd = (double) timePassed / refillPeriodMs * capacity;
            tokens = Math.min(capacity, tokens + tokensToAdd);
            lastRefillTime = now;
        }
    }
}
