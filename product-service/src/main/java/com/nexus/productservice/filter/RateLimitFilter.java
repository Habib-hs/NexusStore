package com.nexus.productservice.filter;

import com.nexus.productservice.service.TokenBucketService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@ConditionalOnProperty(name = "app.rate-limiting.enabled", havingValue = "true")
@RequiredArgsConstructor
@Slf4j
public class RateLimitFilter extends OncePerRequestFilter {

    private final TokenBucketService tokenBucketService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {

        // Get client identifier (IP address for simplicity)
        String clientIdentifier = getClientIdentifier(request);

        // Try to consume a token
        if (!tokenBucketService.tryConsumeToken(clientIdentifier)) {
            // Rate limit exceeded
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Rate limit exceeded. Please try again later.\"}");
            log.warn("Rate limit exceeded for client: {}", clientIdentifier);
            return;
        }

        // Continue with the request
        filterChain.doFilter(request, response);
    }

    private String getClientIdentifier(HttpServletRequest request) {
        // First try to get real IP from X-Forwarded-For header (for load balancers)
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        // Fallback to remote address
        return request.getRemoteAddr();
    }
}
