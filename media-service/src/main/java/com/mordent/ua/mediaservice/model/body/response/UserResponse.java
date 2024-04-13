package com.mordent.ua.mediaservice.model.body.response;

import java.time.Instant;

public record UserResponse(
        Long id,
        String name,
        String surname,
        String username,
        String email,
        String avatar,
        Instant createdAt,
        Instant updatedAt,
        boolean enabled
) {
}
