package in.gov.cybercrime.sachet.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import java.time.Duration;

@Configuration
public class RateLimitingConfig {

    @Bean
    public ApiRateLimitFilter apiRateLimitFilter(
            @Value("${rate-limit.api.enabled:true}") boolean enabled,
            @Value("${rate-limit.api.requests:120}") int maxRequests,
            @Value("${rate-limit.api.window-seconds:60}") long windowSeconds) {
        return new ApiRateLimitFilter(enabled, maxRequests, Duration.ofSeconds(windowSeconds));
    }

    @Bean
    public FilterRegistrationBean<ApiRateLimitFilter> apiRateLimitFilterRegistration(ApiRateLimitFilter filter) {
        FilterRegistrationBean<ApiRateLimitFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(filter);
        registration.addUrlPatterns("/api/*");
        registration.setName("apiRateLimitFilter");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }
}
