package com.mordent.ua.mediaservice.interactor.impl;

import com.mordent.ua.mediaservice.interactor.AlbumInteractor;
import com.mordent.ua.mediaservice.mapper.AlbumMapper;
import com.mordent.ua.mediaservice.mapper.SongMapper;
import com.mordent.ua.mediaservice.model.body.request.AlbumRequest;
import com.mordent.ua.mediaservice.model.body.response.*;
import com.mordent.ua.mediaservice.model.domain.UserSecurity;
import com.mordent.ua.mediaservice.service.AlbumService;
import com.mordent.ua.mediaservice.service.FileService;
import com.mordent.ua.mediaservice.service.SongService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

import static com.mordent.ua.mediaservice.security.JwtFilter.userAllowedToExecute;

@Component
@RequiredArgsConstructor
public class AlbumInteractorImpl implements AlbumInteractor {

    private final AlbumMapper albumMapper;
    private final SongMapper songMapper;
    private final AlbumService albumService;
    private final SongService songService;
    private final FileService fileService;

    @Override
    public Flux<AlbumResponse> findAll(final Long userId, final String name) {
        return albumService.findAll(name)
                .flatMap(album -> Mono.zip(
                                        albumService.findLikeById(userId, album.id()),
                                        songService.findAllSongIdsByAlbumId(album.id()).collect(Collectors.toSet())
                                )
                                .map(tuple -> {
                                    final var like = tuple.getT1();
                                    final var songIds = tuple.getT2();
                                    return albumMapper.toDomainModel(album, like, songIds);
                                })
                                .map(albumMapper::toResponseModel)
                );
    }

    @Override
    public Mono<AlbumResponse> findById(final Long userId, final Long albumId) {
        return Mono.zip(
                        albumService.findById(albumId),
                        albumService.findLikeById(userId, albumId),
                        songService.findAllSongIdsByAlbumId(albumId).collect(Collectors.toSet())
                )
                .map(tuple -> {
                    final var album = tuple.getT1();
                    final var like = tuple.getT2();
                    final var songIds = tuple.getT3();
                    return albumMapper.toDomainModel(album, like, songIds);
                })
                .map(albumMapper::toResponseModel);
    }

    @Override
    public Mono<AlbumStatisticResponse> getStatisticById(final Long albumId) {
        return albumService.findById(albumId)
                .flatMap(album -> Mono.zip(
                                albumService.getCountAlbumLikes(album.id()),
                                albumService.getTotalPlaybackByAlbumId(album.id()),
                                albumService.getCountSongsLikesInAlbum(album.id())
                                )
                                .map(tuple -> {
                                    final var albumLikes = tuple.getT1();
                                    final var songsPlaybacks = tuple.getT2();
                                    final var songsLikes = tuple.getT3();
                                    return new AlbumStatisticResponse(album.id(), albumLikes, songsPlaybacks, songsLikes);
                                })
                );
    }

    @Override
    public Flux<AlbumResponse> findAllByUserId(final Long userId) {
        return albumService.findAllByUserId(userId)
                .flatMap(album -> Mono.zip(
                                        albumService.findLikeById(userId, album.id()),
                                        songService.findAllSongIdsByAlbumId(album.id()).collect(Collectors.toSet())
                                )
                                .map(tuple -> {
                                    final var like = tuple.getT1();
                                    final var songIds = tuple.getT2();
                                    return albumMapper.toDomainModel(album, like, songIds);
                                })
                                .map(albumMapper::toResponseModel)
                );
    }

    @Override
    public Flux<SongWithMetadataResponse> findAllSongs(final Long userId, final Long albumId) {
        return albumService.findById(albumId)
                .flatMapMany(album -> songService.findAllByAlbumId(userId, albumId))
                .map(songMapper::toResponseModel);
    }

    @Override
    public Flux<AlbumResponse> findAllLiked(final Long userId) {
        return albumService.findAllLiked(userId)
                .flatMap(like -> Mono.zip(
                                        albumService.findById(like.id()),
                                        songService.findAllSongIdsByAlbumId(like.id()).collect(Collectors.toSet())
                                )
                                .map(tuple -> {
                                    final var album = tuple.getT1();
                                    final var songIds = tuple.getT2();
                                    return albumMapper.toDomainModel(album, like, songIds);
                                })
                                .map(albumMapper::toResponseModel)
                );
    }

    @Override
    public Mono<AlbumResponse> save(final AlbumRequest request, final FilePart image) {
        return albumService.save(albumMapper.toDataModel(request))
                .map(albumMapper::toDomainModel)
                .flatMap(album -> {
                    if (image != null) {
                        return fileService.updateAlbum(album, image)
                                .thenReturn(album);
                    }
                    return Mono.just(album);
                })
                .map(albumMapper::toResponseModel);
    }

    @Override
    public Mono<LikeResponse> toggleLike(final Long userId, final Long albumId) {
        return albumService.findById(albumId)
                .flatMap(album -> albumService.toggleLike(userId, album.id()))
                .map(like -> new LikeResponse(albumId, like));
    }

    @Override
    public Mono<AlbumSongResponse> toggleSong(final Long userId, final Long albumId, final Long songId) {
        return Mono.empty();
        /*return albumService.toggleSong(userId, albumId, songId)
                .map(saved -> new AlbumSongResponse(albumId, songId, saved));*/
    }

    @Override
    public Mono<AlbumResponse> update(final UserSecurity userSecurity, final AlbumRequest request) {
        return albumService.findById(request.id())
                .flatMap(album -> {
                    userAllowedToExecute(album.userId(), userSecurity, "User cannot update album that is not his own");
                    return Mono.zip(
                            albumService.update(album, albumMapper.toDataModel(request)),
                            albumService.findLikeById(userSecurity.id(), album.id()),
                            songService.findAllSongIdsByAlbumId(album.id()).collect(Collectors.toSet())
                    );
                })
                .map(tuple -> {
                    final var album = tuple.getT1();
                    final var like = tuple.getT2();
                    final var songIds = tuple.getT3();
                    return albumMapper.toDomainModel(album, like, songIds);
                })
                .map(albumMapper::toResponseModel);
    }

    @Override
    public Mono<Void> delete(final UserSecurity userSecurity, final Long albumId) {
        return albumService.findById(albumId)
                .flatMap(album -> {
                    userAllowedToExecute(album.userId(), userSecurity, "User cannot delete album that is not his own");
                    return albumService.delete(album);
                });
    }
}
