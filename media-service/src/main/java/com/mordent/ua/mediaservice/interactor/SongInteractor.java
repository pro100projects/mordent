package com.mordent.ua.mediaservice.interactor;

import com.mordent.ua.mediaservice.model.body.request.SongRequest;
import com.mordent.ua.mediaservice.model.body.response.*;
import com.mordent.ua.mediaservice.model.domain.UserSecurity;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SongInteractor {

    Flux<SongWithMetadataResponse> findAll(Long userId, String name);

    Mono<SongWithMetadataResponse> findById(Long userId, Long songId);

    Mono<SongStatisticResponse> getStatisticById(Long songId);

    Flux<SongWithMetadataResponse> findAllLiked(Long userId);

    Mono<SongResponse> save(UserSecurity userSecurity, SongRequest request, FilePart photo, FilePart song);

    Mono<ListenResponse> listen(UserSecurity userSecurity, Long songId);

    Mono<LikeResponse> toggleLike(Long userId, Long songId);

    Mono<SongWithMetadataResponse> update(UserSecurity userSecurity, SongRequest request);

    Mono<Void> delete(UserSecurity userSecurity, Long songId);
}
