package com.mordent.ua.mediaservice.model.data;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

import static com.mordent.ua.mediaservice.utils.Nulls.getOrDefault;

@Table(name = "playlists")
@Builder(toBuilder = true)
public record Playlist(
        @Id Long id,
        Long userId,
        String name,
        String description,
        String imageFilename,
        boolean isPrivate,
        Instant createdAt,
        Instant updatedAt
) {

    public Playlist overwritingVariables(Playlist playlist) {
        return this.toBuilder()
                .name(getOrDefault(playlist.name, this.name))
                .description(getOrDefault(playlist.description, this.description))
                .imageFilename(getOrDefault(playlist.imageFilename, this.imageFilename))
                .isPrivate(getOrDefault(playlist.isPrivate, this.isPrivate))
                .updatedAt(getOrDefault(playlist.updatedAt, this.updatedAt))
                .build();
    }
}
