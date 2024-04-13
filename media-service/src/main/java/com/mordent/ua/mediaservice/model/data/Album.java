package com.mordent.ua.mediaservice.model.data;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

import static com.mordent.ua.mediaservice.utils.Nulls.getOrDefault;

@Table(name = "albums")
@Builder(toBuilder = true)
public record Album(
        @Id Long id,
        Long userId,
        String name,
        String description,
        String imageFilename,
        Instant createdAt,
        Instant updatedAt
) {

    public Album overwritingVariables(Album album) {
        return this.toBuilder()
                .name(getOrDefault(album.name, this.name))
                .description(getOrDefault(album.description, this.description))
                .imageFilename(getOrDefault(album.imageFilename, this.imageFilename))
                .updatedAt(getOrDefault(album.updatedAt, this.updatedAt))
                .build();
    }
}
