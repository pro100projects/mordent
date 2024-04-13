package com.mordent.ua.mediaservice.security;

import com.mordent.ua.mediaservice.model.domain.ErrorCode;
import com.mordent.ua.mediaservice.model.domain.Role;
import com.mordent.ua.mediaservice.model.domain.UserSecurity;
import com.mordent.ua.mediaservice.model.exception.MediaException;
import reactor.core.publisher.Mono;

import java.util.Objects;

public interface JwtFilter {

    Mono<Boolean> validateToken(String token);

    Mono<Long> getUserIdFromToken(String token);

    static void userAllowedToExecute(final Long ownerId, final UserSecurity user, final String message) {
        if (!Objects.equals(ownerId, user.id()) && !user.roles().contains(Role.ROLE_ADMIN)) {
            throw new MediaException(ErrorCode.NOT_ALLOWED, message);
        }
    }

    static void userAllowedToExecute(final boolean condition, final UserSecurity user, final String message) {
        if (condition && !user.roles().contains(Role.ROLE_ADMIN)) {
            throw new MediaException(ErrorCode.NOT_ALLOWED, message);
        }
    }
}
