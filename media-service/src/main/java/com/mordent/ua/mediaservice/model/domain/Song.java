package com.mordent.ua.mediaservice.model.domain;

import java.time.Instant;

public record Song(
        Long id,
        Long userId,
        Long albumId,
        String name,
        String text,
        String imageFilename,
        String songFilename,
        Long playback,
        Instant createdAt,
        Instant updatedAt
) {
}
