package com.mordent.ua.mediaservice.interactor;

import com.mordent.ua.mediaservice.model.body.request.AlbumRequest;
import com.mordent.ua.mediaservice.model.body.response.*;
import com.mordent.ua.mediaservice.model.domain.UserSecurity;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AlbumInteractor {

    Flux<AlbumResponse> findAll(Long userId, String name);

    Mono<AlbumResponse> findById(Long userId, Long albumId);

    Mono<AlbumStatisticResponse> getStatisticById(Long albumId);

    Flux<AlbumResponse> findAllByUserId(Long userId);

    Flux<SongWithMetadataResponse> findAllSongs(Long userId, Long albumId);

    Flux<AlbumResponse> findAllLiked(Long userId);

    Mono<AlbumResponse> save(AlbumRequest request, FilePart image);

    Mono<LikeResponse> toggleLike(Long userId, Long albumId);

    Mono<AlbumSongResponse> toggleSong(Long userId, Long albumId, Long songId);

    Mono<AlbumResponse> update(UserSecurity userSecurity, AlbumRequest request);

    Mono<Void> delete(UserSecurity userSecurity, Long albumId);
}
