package com.mordent.ua.mediaservice.model.data;

import java.time.Instant;

public record PlaylistSong(
        Long playlistId,
        Long songId,
        Instant timestamp
) {
}
