package com.mordent.ua.mediaservice.model.domain;

import lombok.Builder;

import java.time.Instant;
import java.util.Set;

@Builder(toBuilder = true)
public record Playlist(
        Long id,
        Long userId,
        String name,
        String description,
        String imageFilename,
        boolean isPrivate,
        Instant createdAt,
        Instant updatedAt,
        boolean liked,
        Instant timestamp,
        Set<Long> songIds
) {
}
