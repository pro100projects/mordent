package com.mordent.ua.mediaservice.model.domain;

import lombok.Builder;
import org.springframework.data.annotation.Id;

import java.time.Instant;
import java.util.Set;

@Builder(toBuilder = true)
public record Album(
        @Id Long id,
        Long userId,
        String name,
        String description,
        String imageFilename,
        Instant createdAt,
        Instant updatedAt,
        boolean liked,
        Instant timestamp,
        Set<Long> songIds
) {
}
