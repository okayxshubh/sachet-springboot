package in.gov.cybercrime.sachet.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ApiRateLimitFilter extends OncePerRequestFilter {

    private static final String TOO_MANY_REQUESTS_BODY =
            "{\"message\":\"Too many requests. Please try again later.\"}";
    private static final int CLEANUP_INTERVAL = 256;

    private final boolean enabled;
    private final int maxRequests;
    private final long windowMillis;
    private final Map<String, RequestBucket> buckets = new ConcurrentHashMap<>();
    private final AtomicInteger cleanupCounter = new AtomicInteger();

    public ApiRateLimitFilter(boolean enabled, int maxRequests, Duration window) {
        this.enabled = enabled;
        this.maxRequests = maxRequests;
        this.windowMillis = window.toMillis();
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (!enabled) {
            return true;
        }

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        return !request.getRequestURI().startsWith("/api/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        long now = System.currentTimeMillis();
        String clientKey = resolveClientKey(request);
        RequestBucket bucket = buckets.computeIfAbsent(clientKey, ignored -> new RequestBucket());

        if (!bucket.tryConsume(now, maxRequests, windowMillis)) {
            long retryAfterSeconds = Math.max(1, bucket.retryAfterSeconds(now, windowMillis));
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Retry-After", String.valueOf(retryAfterSeconds));
            response.getWriter().write(TOO_MANY_REQUESTS_BODY);
            return;
        }

        cleanupIfNeeded(now);
        filterChain.doFilter(request, response);
    }

    private String resolveClientKey(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }

        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isBlank()) {
            return realIp.trim();
        }

        return request.getRemoteAddr();
    }

    private void cleanupIfNeeded(long now) {
        if (cleanupCounter.incrementAndGet() % CLEANUP_INTERVAL != 0) {
            return;
        }

        buckets.entrySet().removeIf(entry -> entry.getValue().isExpired(now, windowMillis));
    }

    static final class RequestBucket {
        private final Deque<Long> requestTimestamps = new ArrayDeque<>();

        synchronized boolean tryConsume(long now, int maxRequests, long windowMillis) {
            evictExpired(now, windowMillis);
            if (requestTimestamps.size() >= maxRequests) {
                return false;
            }

            requestTimestamps.addLast(now);
            return true;
        }

        synchronized long retryAfterSeconds(long now, long windowMillis) {
            evictExpired(now, windowMillis);
            Long oldest = requestTimestamps.peekFirst();
            if (oldest == null) {
                return 1;
            }

            long waitMillis = Math.max(1000, (oldest + windowMillis) - now);
            return (waitMillis + 999) / 1000;
        }

        synchronized boolean isExpired(long now, long windowMillis) {
            evictExpired(now, windowMillis);
            return requestTimestamps.isEmpty();
        }

        private void evictExpired(long now, long windowMillis) {
            long threshold = now - windowMillis;
            while (!requestTimestamps.isEmpty() && requestTimestamps.peekFirst() <= threshold) {
                requestTimestamps.removeFirst();
            }
        }
    }
}
