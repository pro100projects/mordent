package com.mordent.ua.mediaservice.health.impl;

import com.mordent.ua.mediaservice.health.BaseReactiveHealthIndicator;
import com.mordent.ua.mediaservice.service.health.AuthHealthService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!mockAuth")
public class AuthServiceHealthIndicator extends BaseReactiveHealthIndicator {

    public AuthServiceHealthIndicator(final AuthHealthService healthService) {
        super(healthService);
    }
}
