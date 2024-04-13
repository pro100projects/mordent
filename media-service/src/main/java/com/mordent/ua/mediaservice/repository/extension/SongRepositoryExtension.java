package com.mordent.ua.mediaservice.repository.extension;

import com.mordent.ua.mediaservice.model.data.Like;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SongRepositoryExtension {

    Flux<Like> findAllLikeSongsByUserId(Long userId);

    Mono<Like> findSongLike(Long userId, Long songId);

    Mono<Long> getCountSongLikes(Long songId);

    Mono<Long> getCountSongsLikesInAlbum(Long albumId);

    Mono<Boolean> saveSongLike(Long userId, Long songId);

    Mono<Void> deleteSongLikes(Long songId);

    Mono<Boolean> deleteSongLike(Long userId, Long songId);
}
