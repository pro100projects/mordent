package com.mordent.ua.mediaservice.repository;

import com.mordent.ua.mediaservice.model.data.Playlist;
import com.mordent.ua.mediaservice.repository.extension.PlaylistRepositoryExtension;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface PlaylistRepository extends R2dbcRepository<Playlist, Long>, PlaylistRepositoryExtension {

    Flux<Playlist> findAllByUserId(Long userId);

    Flux<Playlist> findAllByNameIsContainingIgnoreCase(String name);
}
