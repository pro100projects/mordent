package com.mordent.ua.mediaservice.model.body.response;

public record AlbumStatisticResponse(
        Long id,
        Long likes,
        Long songsPlaybacks,
        Long songsLikes
) {
}
