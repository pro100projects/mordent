package com.mordent.ua.mediaservice.service;

import com.mordent.ua.mediaservice.model.data.Album;
import com.mordent.ua.mediaservice.model.data.Like;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AlbumService {

    Flux<Album> findAll(String name);

    Mono<Album> findById(Long albumId);

    Flux<Album> findAllByUserId(Long userId);

    Flux<Like> findAllLiked(Long userId);

    Mono<Like> findLikeById(Long userId, Long albumId);

    Mono<Album> save(Album album);

    Mono<Boolean> toggleLike(Long userId, Long albumId);

    //Mono<Boolean> toggleSong(Long userId, Long albumId, Long songId);

    Mono<Album> update(Album oldAlbum, Album newAlbum);

    Mono<Void> delete(Album album);

    Mono<Long> getCountAlbumLikes(Long albumId);

    Mono<Long> getTotalPlaybackByAlbumId(Long albumId);

    Mono<Long> getCountSongsLikesInAlbum(Long albumId);
}
