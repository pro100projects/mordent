package com.mordent.ua.mediaservice.kafka.event;

import com.mordent.ua.mediaservice.model.domain.Song;
import com.mordent.ua.mediaservice.model.domain.UserSecurity;

public record SongEvent(
        UserData user,
        SongData song
) {

    public SongEvent(final UserSecurity userSecurity, final Song song, final String filepath) {
        this(
                new UserData(userSecurity.id(), userSecurity.name(), userSecurity.surname(), userSecurity.username(), userSecurity.email()),
                new SongData(song.id(), song.name(), song.songFilename(), null, filepath)
        );
    }

    public SongEvent(final UserSecurity userSecurity, final Song song) {
        this(
                new UserData(userSecurity.id(), userSecurity.name(), userSecurity.surname(), userSecurity.username(), userSecurity.email()),
                new SongData(song.id(), song.name(), song.songFilename(), song.playback(), null)
        );
    }

    record UserData(
            Long id,
            String name,
            String surname,
            String username,
            String email
    ) {
    }

    record SongData(
            Long id,
            String name,
            String filename,
            Long playback,
            String filepath
    ) {
    }
}
