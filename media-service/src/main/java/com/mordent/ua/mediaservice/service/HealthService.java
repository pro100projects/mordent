package com.mordent.ua.mediaservice.service;

import org.springframework.boot.actuate.health.Health;
import reactor.core.publisher.Mono;

public interface HealthService {

    Mono<Health> getHealth();
}
