package com.mordent.ua.metadataservice.service.impl;

import com.mordent.ua.metadataservice.model.data.SongMetadata;
import com.mordent.ua.metadataservice.repository.SongMetadataRepository;
import com.mordent.ua.metadataservice.service.SongService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class SongServiceImpl implements SongService {

    private final SongMetadataRepository songMetadataRepository;

    @Override
    public Mono<SongMetadata> save(final SongMetadata songMetadata) {
        return songMetadataRepository.save(songMetadata);
    }

    @Override
    public Mono<Void> delete(final Long songId) {
        return songMetadataRepository.deleteById(songId);
    }
}
