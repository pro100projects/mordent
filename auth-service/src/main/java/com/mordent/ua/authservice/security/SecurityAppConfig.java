package com.mordent.ua.authservice.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Optional;

@ConfigurationProperties(prefix = "security")
public record SecurityAppConfig(
        Jwt jwt,
        Oauth2 oauth2,
        Activate activate
) {

    public record Jwt(
            String secret
    ) {}

    public record Oauth2(
            Optional<String> authorizedRequestHost,
            List<String> authorizedRequestHosts,
            List<String> authorizedRedirectUris
    ) {}

    public record Activate(
            String redirectUri
    ) {}
}
