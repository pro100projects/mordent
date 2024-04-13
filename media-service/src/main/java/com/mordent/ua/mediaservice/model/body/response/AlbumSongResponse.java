package com.mordent.ua.mediaservice.model.body.response;

public record AlbumSongResponse(
        Long playlistId,
        Long songId,
        boolean saved
) {
}
