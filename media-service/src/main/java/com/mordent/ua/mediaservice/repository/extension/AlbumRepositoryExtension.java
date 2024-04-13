package com.mordent.ua.mediaservice.repository.extension;

import com.mordent.ua.mediaservice.model.data.Like;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AlbumRepositoryExtension {

    Flux<Like> findAllLikeAlbumsByUserId(Long userId);

    Mono<Like> findAlbumLike(Long userId, Long albumId);

    Mono<Long> getCountAlbumLikes(Long albumId);

    Mono<Boolean> saveAlbumLike(Long userId, Long albumId);

    Mono<Void> deleteAlbumLikes(Long albumId);

    Mono<Boolean> deleteAlbumLike(Long userId, Long albumId);
}
