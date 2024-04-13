package com.mordent.ua.authservice.kafka.event;

import java.time.Instant;
import java.util.UUID;

public record UserEvent(
        Long id,
        String name,
        String surname,
        String username,
        String email,
        Instant createdAt,
        Instant updatedAt,
        boolean enabled,
        UUID uuid
) {
}
