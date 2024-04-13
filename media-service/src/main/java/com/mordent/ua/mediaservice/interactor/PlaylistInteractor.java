package com.mordent.ua.mediaservice.interactor;

import com.mordent.ua.mediaservice.model.body.request.PlaylistRequest;
import com.mordent.ua.mediaservice.model.body.response.*;
import com.mordent.ua.mediaservice.model.domain.UserSecurity;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PlaylistInteractor {

    Flux<PlaylistResponse> findAll(Long userId, String name);

    Mono<PlaylistResponse> findById(UserSecurity userSecurity, Long playlistId);

    Mono<PlaylistStatisticResponse> getStatisticById(UserSecurity userSecurity, Long playlistId);

    Flux<PlaylistResponse> findAllByUserId(Long userId);

    Flux<SongWithMetadataResponse> findAllSongs(UserSecurity userSecurity, Long playlistId);

    Flux<PlaylistResponse> findAllLiked(Long userId);

    Mono<PlaylistResponse> save(PlaylistRequest request, FilePart image);

    Mono<LikeResponse> toggleLike(Long userId, Long playlistId);

    Mono<PlaylistSongResponse> toggleSong(UserSecurity userSecurity, Long playlistId, Long songId);

    Mono<PlaylistResponse> update(UserSecurity userSecurity, PlaylistRequest request);

    Mono<Void> delete(UserSecurity userSecurity, Long playlistId);
}
