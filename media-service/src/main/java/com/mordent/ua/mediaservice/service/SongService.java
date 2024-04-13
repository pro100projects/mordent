package com.mordent.ua.mediaservice.service;

import com.mordent.ua.mediaservice.model.data.Like;
import com.mordent.ua.mediaservice.model.data.Song;
import com.mordent.ua.mediaservice.model.data.SongMetadata;
import com.mordent.ua.mediaservice.model.domain.SongStatistic;
import com.mordent.ua.mediaservice.model.domain.SongWithMetadata;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

public interface SongService {

    Flux<Song> findAll(String name);

    Mono<Song> findById(Long songId);

    Mono<SongMetadata> findSongMetadataById(Long songId);

    Mono<Like> findSongLike(Long userId, Long songId);

    Mono<SongStatistic> getStatisticById(Long songId);

    Flux<Tuple2<Like, Song>> findAllLiked(Long userId);

    Mono<Song> save(Song song);

    Mono<Song> listen(Long songId);

    Mono<Boolean> toggleLike(Long userId, Long songId);

    Mono<Song> update(Song oldSong, Song newSong);

    Mono<Void> delete(Song song);

    Flux<SongWithMetadata> findAllByAlbumId(Long userId, Long albumId);

    Flux<Long> findAllSongIdsByAlbumId(Long albumId);

    Flux<SongWithMetadata> findAllByPlaylistId(Long userId, Long playlistId);

    Flux<Long> findAllSongIdsByPlaylistId(Long playlistId);
}
