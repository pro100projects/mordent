package com.mordent.ua.mediaservice.interactor.impl;

import com.mordent.ua.mediaservice.interactor.SongInteractor;
import com.mordent.ua.mediaservice.kafka.event.SongEvent;
import com.mordent.ua.mediaservice.kafka.producer.SongKafkaProducer;
import com.mordent.ua.mediaservice.mapper.SongMapper;
import com.mordent.ua.mediaservice.model.body.request.SongRequest;
import com.mordent.ua.mediaservice.model.body.response.*;
import com.mordent.ua.mediaservice.model.data.Album;
import com.mordent.ua.mediaservice.model.data.Like;
import com.mordent.ua.mediaservice.model.data.Song;
import com.mordent.ua.mediaservice.model.data.SongMetadata;
import com.mordent.ua.mediaservice.model.domain.ErrorCode;
import com.mordent.ua.mediaservice.model.domain.SongWithMetadata;
import com.mordent.ua.mediaservice.model.domain.UserSecurity;
import com.mordent.ua.mediaservice.model.exception.MediaException;
import com.mordent.ua.mediaservice.service.AlbumService;
import com.mordent.ua.mediaservice.service.FileService;
import com.mordent.ua.mediaservice.service.SongService;
import com.mordent.ua.mediaservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.HashMap;

import static com.mordent.ua.mediaservice.security.JwtFilter.userAllowedToExecute;

@Slf4j
@Component
@RequiredArgsConstructor
public class SongInteractorImpl implements SongInteractor {

    private final SongMapper songMapper;
    private final SongService songService;
    private final UserService userService;
    private final AlbumService albumService;
    private final FileService fileService;
    private final SongKafkaProducer songKafkaProducer;

    @Override
    public Flux<SongWithMetadataResponse> findAll(final Long userId, final String name) {
        return songService.findAll(name)
                .flatMap(song -> mapToSongWithMetadata(userId, song))
                .map(songMapper::toResponseModel);
    }

    @Override
    public Mono<SongWithMetadataResponse> findById(final Long userId, final Long songId) {
        return songService.findById(songId)
                .flatMap(song -> mapToSongWithMetadata(userId, song))
                .map(songMapper::toResponseModel);
    }

    @Override
    public Mono<SongStatisticResponse> getStatisticById(final Long songId) {
        return songService.getStatisticById(songId)
                .map(songMapper::toResponseModel);
    }

    @Override
    public Flux<SongWithMetadataResponse> findAllLiked(final Long userId) {
        return songService.findAllLiked(userId)
                .flatMap(tuple -> mapToSongWithMetadata(userId, tuple.getT2(), tuple.getT1()))
                .map(songMapper::toResponseModel);
    }

    @Override
    public Mono<SongResponse> save(final UserSecurity userSecurity, final SongRequest request, final FilePart photoFilePart, final FilePart songFilePart) {
        return songService.save(songMapper.toDataModel(request))
                .map(songMapper::toDomainModel)
                .flatMap(song -> Mono.zip(
                                userService.findById(song.userId()),
                                song.albumId() != null ? albumService.findById(song.albumId()) : Mono.just(new Album(null, null, null, null, null, null, null)),
                                fileService.saveSong(song, photoFilePart, songFilePart)
                                        .onErrorResume(error -> songService.delete(songMapper.toDataModel(song))
                                                .then(Mono.error(new MediaException(ErrorCode.UNEXPECTED, error.getMessage()))))
                        )
                        .flatMap(tuple -> {
                            final var user = tuple.getT1();
                            final var album = tuple.getT2();
                            final var filepath = tuple.getT3();
                            return songKafkaProducer.sendSaveEvent(new SongEvent(userSecurity, song, filepath))
                                    .thenReturn(songMapper.toResponseModel(song, user, album));
                        }));
    }

    @Override
    public Mono<ListenResponse> listen(final UserSecurity userSecurity, final Long songId) {
        return songService.listen(songId)
                .map(songMapper::toDomainModel)
                .flatMap(song -> {
                    Long playback = 100L;
                    while(song.playback() >= playback) {
                        if (song.playback().equals(playback)) {
                            return songKafkaProducer.sendListenEvent(new SongEvent(userSecurity, song))
                                    .thenReturn(song);
                        }
                        playback *= 10L;
                    }
                    return Mono.just(song);
                })
                .map(songMapper::toListenResponse);
    }

    @Override
    public Mono<LikeResponse> toggleLike(final Long userId, final Long songId) {
        return songService.toggleLike(userId, songId)
                .map(like -> new LikeResponse(songId, like));
    }

    @Override
    public Mono<SongWithMetadataResponse> update(final UserSecurity userSecurity, final SongRequest request) {
        return songService.findById(request.id())
                .flatMap(song -> {
                    userAllowedToExecute(song.userId(), userSecurity, "User cannot update song that is not his own");
                    return songService.update(song, songMapper.toDataModel(request));
                })
                .flatMap(song -> mapToSongWithMetadata(request.userId(), song))
                .map(songMapper::toResponseModel);
    }

    @Override
    public Mono<Void> delete(final UserSecurity userSecurity, final Long songId) {
        return songService.findById(songId)
                .flatMap(song -> {
                    userAllowedToExecute(song.userId(), userSecurity, "User cannot delete song that is not his own");
                    return songService.delete(song);
                })
                .publishOn(Schedulers.boundedElastic())
                .doOnSuccess(s -> songKafkaProducer.sendDeleteEvent(songId).subscribe());
    }

    private Mono<SongWithMetadata> mapToSongWithMetadata(final Long userId, final Song song, final Like... likeSong) {
        return Mono.zip(
                        userService.findById(song.userId()),
                        song.albumId() != null ? albumService.findById(song.albumId()) : Mono.just(new Album(null, null, null, null, null, null, null)),
                        likeSong.length == 0 ? songService.findSongLike(userId, song.id()) : Mono.just(likeSong[0]),
                        songService.findSongMetadataById(song.id())
                                .switchIfEmpty(Mono.just(new SongMetadata(song.id(), new HashMap<>())))
                )
                .map(tuple -> {
                    final var user = tuple.getT1();
                    final var album = tuple.getT2();
                    final var like = tuple.getT3();
                    final var metadata = tuple.getT4();
                    return new SongWithMetadata(song, user, album, like, metadata);
                });
    }
}
