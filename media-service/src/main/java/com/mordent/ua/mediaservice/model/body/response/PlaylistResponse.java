package com.mordent.ua.mediaservice.model.body.response;

import java.time.Instant;
import java.util.Set;

public record PlaylistResponse(
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
