package com.mordent.ua.mediaservice.model.body.response;

public record PlaylistSongResponse(
        Long playlistId,
        Long songId,
        boolean saved
) {
}
