package com.mordent.ua.mediaservice.model.domain;

import com.mordent.ua.mediaservice.model.data.Album;
import com.mordent.ua.mediaservice.model.data.Song;
import com.mordent.ua.mediaservice.model.data.User;
import com.mordent.ua.mediaservice.model.data.*;

import java.time.Instant;
import java.util.Map;

public record SongWithMetadata(
        Long id,
        SongUser user,
        SongAlbum album,
        String name,
        String text,
        String imageFilename,
        String songFilename,
        Long playback,
        Instant createdAt,
        Instant updatedAt,
        boolean liked,
        Instant timestamp,
        Map<String, String> metadata
) {

    public record SongUser(
            Long id,
            String name,
            String surname,
            String username
    ) {}

    public record SongAlbum(
            Long id,
            String name
    ) {}

    public SongWithMetadata(Song song, User user, Album album, Like like, SongMetadata songMetadata) {
        this(song.id(), new SongUser(user.id(), user.name(), user.surname(), user.username()), new SongAlbum(album.id(), album.name()), song.name(), song.text(), song.imageFilename(), song.songFilename(), song.playback(), song.createdAt(), song.updatedAt(), like.liked(), like.timestamp(), songMetadata.metadata());
    }

    public SongWithMetadata(Song song, User user, Album album, boolean liked, Instant timestamp, SongMetadata songMetadata) {
        this(song.id(), new SongUser(user.id(), user.name(), user.surname(), user.username()), new SongAlbum(album.id(), album.name()), song.name(), song.text(), song.imageFilename(), song.songFilename(), song.playback(), song.createdAt(), song.updatedAt(), liked, timestamp, songMetadata.metadata());
    }
}
