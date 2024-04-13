package com.mordent.ua.mediaservice.model.body.request;

import lombok.Builder;

@Builder(toBuilder = true)
public record PlaylistRequest(
        Long id,
        Long userId,
        String name,
        String description,
        String imageFilename,
        boolean isPrivate
) {
}
