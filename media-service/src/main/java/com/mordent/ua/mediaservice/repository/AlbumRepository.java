package com.mordent.ua.mediaservice.repository;

import com.mordent.ua.mediaservice.model.data.Album;
import com.mordent.ua.mediaservice.repository.extension.AlbumRepositoryExtension;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface AlbumRepository extends R2dbcRepository<Album, Long>, AlbumRepositoryExtension {

    Flux<Album> findAllByUserId(Long userId);

    @Query("SELECT COUNT(*) > 0 FROM albums WHERE id = :albumId AND user_id = :userId")
    Mono<Boolean> existsByAlbumIdAndUserId(Long albumId, Long userId);

    Flux<Album> findAllByNameIsContainingIgnoreCase(String name);
}
