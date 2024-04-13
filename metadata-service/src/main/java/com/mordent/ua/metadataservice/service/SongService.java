package com.mordent.ua.metadataservice.service;

import com.mordent.ua.metadataservice.model.data.SongMetadata;
import reactor.core.publisher.Mono;

public interface SongService {

    Mono<SongMetadata> save(SongMetadata songMetadata);

    Mono<Void> delete(Long songId);
}
