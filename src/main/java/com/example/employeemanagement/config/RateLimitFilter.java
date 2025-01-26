package com.example.employeemanagement.config;

import io.github.bucket4j.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final ConcurrentMap<String, Bucket> cache = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws java.io.IOException, jakarta.servlet.ServletException {

        String ip = request.getRemoteAddr();
        Bucket bucket = cache.computeIfAbsent(ip, this::newBucket);

        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        if (probe.isConsumed()) {
            // Allowed
            response.addHeader("X-Rate-Limit-Remaining",
                    String.valueOf(probe.getRemainingTokens()));
            filterChain.doFilter(request, response);
        } else {
            // Too many requests
            response.setStatus(429); // HTTP 429 Too Many Requests
            response.getWriter().write("Too many requests - rate limit exceeded.");
        }
    }

    private Bucket newBucket(String key) {
        return Bucket4j.builder()
                .addLimit(Bandwidth.classic(10, Refill.greedy(10, Duration.ofSeconds(60))))
                .build();
    }
}
