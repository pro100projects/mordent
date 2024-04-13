package com.mordent.ua.mediaservice.configuration.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "services")
public record ServiceProperties(
        AuthServiceProperties auth
) {

    public record AuthServiceProperties(String url) {}
}
