package com.mordent.ua.mediaservice.configuration;

import com.mordent.ua.mediaservice.configuration.property.ServiceProperties;
import com.mordent.ua.mediaservice.model.Qualifiers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfiguration {

    @Bean(Qualifiers.AUTH_WEB_CLIENT)
    public WebClient authWebClient(final ServiceProperties serviceProperties) {
        return WebClient.builder().baseUrl(serviceProperties.auth().url()).build();
    }
}
