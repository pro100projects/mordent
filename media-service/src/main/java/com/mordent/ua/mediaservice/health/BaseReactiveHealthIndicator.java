package com.mordent.ua.mediaservice.health;

import com.mordent.ua.mediaservice.service.HealthService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public abstract class BaseReactiveHealthIndicator implements ReactiveHealthIndicator {

    protected final HealthService healthService;

    @Override
    public Mono<Health> health() {
        return healthService.getHealth();
    }
}
