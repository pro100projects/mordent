package com.mordent.ua.notificationservice.kafka.event;

public record SongEvent(
        UserData user,
        SongData song
) {

    public record UserData(
            Long id,
            String name,
            String surname,
            String username,
            String email
    ) {
    }

    public record SongData(
            Long id,
            String name,
            String filename,
            Long playback
    ) {
    }
}
