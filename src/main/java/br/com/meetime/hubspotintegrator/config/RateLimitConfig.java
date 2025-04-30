package br.com.meetime.hubspotintegrator.config;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

import static br.com.meetime.hubspotintegrator.constansts.HubspotConstants.HUBSPOT_API_RATELIMITER;

@Configuration
public class RateLimitConfig {

    @Bean
    public RateLimiter rateLimiter() {
        RateLimiterConfig rateLimiterConfig = RateLimiterConfig.custom()
                .limitForPeriod(100)
                .limitRefreshPeriod(Duration.ofSeconds(10))
                .timeoutDuration(Duration.ofSeconds(5))
                .build();

        return RateLimiter.of(HUBSPOT_API_RATELIMITER, rateLimiterConfig);
    }
}
