package com.mordent.ua.mediaservice.model.data;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

import static com.mordent.ua.mediaservice.utils.Nulls.getOrDefault;

@Table(name = "songs")
@Builder(toBuilder = true)
public record Song(
        @Id Long id,
        Long userId,
        Long albumId,
        String name,
        String text,
        String imageFilename,
        String songFilename,
        Long playback,
        Instant createdAt,
        Instant updatedAt
) {

    public Song overwritingVariables(Song song) {
        return this.toBuilder()
                .name(getOrDefault(song.name, this.name))
                .albumId(getOrDefault(song.albumId, this.albumId))
                .text(getOrDefault(song.text, this.text))
                .imageFilename(getOrDefault(song.imageFilename, this.imageFilename))
                .playback(getOrDefault(song.playback, this.playback))
                .updatedAt(getOrDefault(song.updatedAt, this.updatedAt))
                .build();
    }
}
