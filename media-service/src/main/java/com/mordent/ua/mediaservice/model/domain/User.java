package com.mordent.ua.mediaservice.model.domain;

import lombok.Builder;

import java.time.Instant;

@Builder(toBuilder = true)
public record User(
        Long id,
        String name,
        String surname,
        String username,
        String email,
        String avatar,
        String password,
        Instant createdAt,
        Instant updatedAt,
        boolean enabled
) {
}
