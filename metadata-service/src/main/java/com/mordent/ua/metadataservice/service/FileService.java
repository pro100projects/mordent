package com.mordent.ua.metadataservice.service;

import com.mordent.ua.metadataservice.model.data.SongMetadata;
import reactor.core.publisher.Mono;

public interface FileService {

    Mono<SongMetadata> getSongMetadata(Long id, String filepath);
}
