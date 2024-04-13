package com.mordent.ua.mediaservice.service.health;

import com.mordent.ua.mediaservice.model.Qualifiers;
import com.mordent.ua.mediaservice.model.domain.HealthResponse;
import com.mordent.ua.mediaservice.service.HealthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.health.Health;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@Profile("!mockAuth")
@RequiredArgsConstructor
public class AuthHealthService implements HealthService {

    private static final String PATH_HEALTH = "actuator/health";

    @Qualifier(Qualifiers.AUTH_WEB_CLIENT)
    private final WebClient webClient;

    @Override
    public Mono<Health> getHealth() {
        return webClient.get()
                .uri(PATH_HEALTH)
                .retrieve()
                .bodyToMono(HealthResponse.class)
                .map(response -> Health.up().build())
                .onErrorResume(error -> Mono.just(Health.down().build()));
    }

}
