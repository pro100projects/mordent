package com.mordent.ua.mediaservice.repository;

import com.mordent.ua.mediaservice.model.data.Song;
import com.mordent.ua.mediaservice.repository.extension.SongRepositoryExtension;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface SongRepository extends R2dbcRepository<Song, Long>, SongRepositoryExtension {

    Flux<Song> findAllByAlbumId(Long albumId);

    Flux<Song> findAllByNameIsContainingIgnoreCase(String name);

    @Query("SELECT SUM(playback) AS total_playback FROM songs WHERE album_id = :albumId")
    Mono<Long> getTotalPlaybackByAlbumId(Long albumId);
}
