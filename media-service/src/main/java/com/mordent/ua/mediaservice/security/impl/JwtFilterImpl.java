package com.mordent.ua.mediaservice.security.impl;

import com.mordent.ua.mediaservice.model.Qualifiers;
import com.mordent.ua.mediaservice.model.body.response.ValidateTokenResponse;
import com.mordent.ua.mediaservice.security.JwtFilter;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.text.ParseException;

@Component
@Profile("!mockAuth")
@RequiredArgsConstructor
public class JwtFilterImpl implements JwtFilter {

    private static final String PATH_VALIDATE_TOKEN = "/api/auth/token";

    @Qualifier(Qualifiers.AUTH_WEB_CLIENT)
    private final WebClient webClient;

    @Override
    public Mono<Boolean> validateToken(final String token) {
        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(PATH_VALIDATE_TOKEN)
                        .queryParam("token", "Bearer " + token)
                        .build()
                )
                .exchangeToMono(response -> response.bodyToMono(ValidateTokenResponse.class)
                        .map(ValidateTokenResponse::validated));
    }

    @Override
    public Mono<Long> getUserIdFromToken(final String token) {
        try {
            return Mono.just(SignedJWT.parse(token).getJWTClaimsSet().getLongClaim("id"));
        } catch (ParseException ex) {
            throw new RuntimeException(ex);
        }
    }
}
