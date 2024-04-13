package com.mordent.ua.mediaservice.service;

import com.mordent.ua.mediaservice.model.data.Like;
import com.mordent.ua.mediaservice.model.data.Playlist;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PlaylistService {

    Flux<Playlist> findAll(String name);

    Mono<Playlist> findById(Long playlistId);

    Flux<Playlist> findAllByUserId(Long userId);

    Flux<Like> findAllLiked(Long userId);

    Mono<Like> findLikeById(Long userId, Long playlistId);

    Mono<Playlist> save(Playlist playlist);

    Mono<Boolean> toggleLike(Long userId, Long playlistId);

    Mono<Boolean> toggleSong(Long playlistId, Long songId);

    Mono<Playlist> update(Playlist oldPlaylist, Playlist newPlaylist);

    Mono<Void> delete(Playlist playlist);

    Mono<Long> getCountPlaylistLikes(Long playlistId);
}
