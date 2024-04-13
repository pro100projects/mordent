package com.mordent.ua.mediaservice.model.body.response;

import com.mordent.ua.mediaservice.model.domain.SongWithMetadata;

import java.time.Instant;

public record SongResponse(
        Long id,
        SongWithMetadata.SongUser user,
        SongWithMetadata.SongAlbum album,
        String name,
        String text,
        String imageFilename,
        String songFilename,
        Long playback,
        Instant createdAt,
        Instant updatedAt,
        boolean liked,
        Instant timestamp
) {
}
