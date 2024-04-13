package com.mordent.ua.mediaservice.model.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Document
public record SongMetadata(
    @Id Long id,
    Map<String, String> metadata
) {
}