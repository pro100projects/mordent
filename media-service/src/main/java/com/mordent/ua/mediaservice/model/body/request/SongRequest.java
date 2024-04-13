package com.mordent.ua.mediaservice.model.body.request;

import lombok.Builder;

@Builder(toBuilder = true)
public record SongRequest(
        Long id,
        Long userId,
        Long albumId,
        String name,
        String text,
        String imageFilename,
        String songFilename
) {
}
