package com.iury.backendsenior.config;

import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.BucketConfiguration;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.function.Supplier;

public class DistributedRateLimitFilter extends OncePerRequestFilter {

    private final ProxyManager<String> proxyManager;
    private final Supplier<BucketConfiguration> bucketConfigSupplier;
    private final String keyPrefix;
    private final int rpm;
    private final boolean enabled;

    public DistributedRateLimitFilter(ProxyManager<String> proxyManager, Supplier<BucketConfiguration> bucketConfigSupplier, String keyPrefix, int rpm, boolean enabled) {
        this.proxyManager = proxyManager;
        this.bucketConfigSupplier = bucketConfigSupplier;
        this.keyPrefix = keyPrefix;
        this.rpm = rpm;
        this.enabled = enabled;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (!enabled) return true;
        String path = request.getRequestURI();
        return path.startsWith("/actuator") ||
               path.startsWith("/swagger") || 
               path.startsWith("/v3/api-docs");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String key = keyPrefix + ":" + resolveKey(request);

        try {
            ConsumptionProbe probe = proxyManager.builder().build(key, bucketConfigSupplier).tryConsumeAndReturnRemaining(1);

            response.setHeader("X-Rate-Limit-Limit", String.valueOf(rpm));
            response.setHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));

            if (probe.isConsumed()) {
                filterChain.doFilter(request, response);
                return;
            }

            response.setStatus(429);
            response.setHeader(HttpHeaders.RETRY_AFTER, String.valueOf(Math.max(1, probe.getNanosToWaitForRefill() / 1_000_000_000L)));
            response.setContentType("application/json");
            response.getWriter().write("{\"message\":\"Rate limit excedido. Tente novamente mais tarde.\"}");
        } catch (Exception e) {
            filterChain.doFilter(request, response);
        }
    }

    private String resolveKey(HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            return "user:" + auth.getName();
        }
        return "ip:" + request.getRemoteAddr();
    }
}