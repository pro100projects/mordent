package com.mordent.ua.mediaservice.security.mock;

import com.mordent.ua.mediaservice.security.JwtFilter;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.text.ParseException;

@Component
@Profile("mockAuth")
@RequiredArgsConstructor
public class JwtFilterMock implements JwtFilter {

    @Override
    public Mono<Boolean> validateToken(final String token) {
        return Mono.just(true);
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
