package com.mordent.ua.mediaservice.interactor.impl;

import com.mordent.ua.mediaservice.interactor.PlaylistInteractor;
import com.mordent.ua.mediaservice.mapper.PlaylistMapper;
import com.mordent.ua.mediaservice.mapper.SongMapper;
import com.mordent.ua.mediaservice.model.body.request.PlaylistRequest;
import com.mordent.ua.mediaservice.model.body.response.*;
import com.mordent.ua.mediaservice.model.domain.UserSecurity;
import com.mordent.ua.mediaservice.service.FileService;
import com.mordent.ua.mediaservice.service.PlaylistService;
import com.mordent.ua.mediaservice.service.SongService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.stream.Collectors;

import static com.mordent.ua.mediaservice.security.JwtFilter.userAllowedToExecute;

@Component
@RequiredArgsConstructor
public class PlaylistInteractorImpl implements PlaylistInteractor {

    private final PlaylistMapper playlistMapper;
    private final SongMapper songMapper;
    private final PlaylistService playlistService;
    private final SongService songService;
    private final FileService fileService;

    @Override
    public Flux<PlaylistResponse> findAll(final Long userId, final String name) {
        return playlistService.findAll(name)
                .filter(playlist -> !playlist.isPrivate() || Objects.equals(playlist.userId(), userId))
                .flatMap(playlist -> Mono.zip(
                                        playlistService.findLikeById(userId, playlist.id()),
                                        songService.findAllSongIdsByPlaylistId(playlist.id()).collect(Collectors.toSet())
                                )
                                .map(tuple -> {
                                    final var like = tuple.getT1();
                                    final var songIds = tuple.getT2();
                                    return playlistMapper.toDomainModel(playlist, like, songIds);
                                })
                                .map(playlistMapper::toResponseModel)
                );
    }

    @Override
    public Mono<PlaylistResponse> findById(final UserSecurity userSecurity, final Long playlistId) {
        return playlistService.findById(playlistId)
                .flatMap(playlist -> {
                    userAllowedToExecute(playlist.isPrivate() && !Objects.equals(playlist.userId(), userSecurity.id()), userSecurity, "This playlist is private");
                    return Mono.zip(
                                    playlistService.findLikeById(userSecurity.id(), playlist.id()),
                                    songService.findAllSongIdsByPlaylistId(playlist.id()).collect(Collectors.toSet())
                            )
                            .map(tuple -> {
                                final var like = tuple.getT1();
                                final var songIds = tuple.getT2();
                                return playlistMapper.toDomainModel(playlist, like, songIds);
                            })
                            .map(playlistMapper::toResponseModel);
                });
    }

    @Override
    public Mono<PlaylistStatisticResponse> getStatisticById(final UserSecurity userSecurity, final Long playlistId) {
        return playlistService.findById(playlistId)
                .flatMap(playlist -> {
                    userAllowedToExecute(playlist.isPrivate() && !Objects.equals(playlist.userId(), userSecurity.id()), userSecurity, "This playlist is private");
                    return playlistService.getCountPlaylistLikes(playlist.id());
                })
                .map(likes -> new PlaylistStatisticResponse(playlistId, likes));
    }

    @Override
    public Flux<PlaylistResponse> findAllByUserId(final Long userId) {
        return playlistService.findAllByUserId(userId)
                .flatMap(playlist -> Mono.zip(
                                        playlistService.findLikeById(userId, playlist.id()),
                                        songService.findAllSongIdsByPlaylistId(playlist.id()).collect(Collectors.toSet())
                                )
                                .map(tuple -> {
                                    final var like = tuple.getT1();
                                    final var songIds = tuple.getT2();
                                    return playlistMapper.toDomainModel(playlist, like, songIds);
                                })
                                .map(playlistMapper::toResponseModel)
                );
    }

    @Override
    public Flux<SongWithMetadataResponse> findAllSongs(final UserSecurity userSecurity, final Long playlistId) {
        return playlistService.findById(playlistId)
                .flatMapMany(playlist -> {
                    userAllowedToExecute(playlist.isPrivate() && !Objects.equals(playlist.userId(), userSecurity.id()), userSecurity, "This playlist is private");
                    return songService.findAllByPlaylistId(userSecurity.id(), playlistId);
                })
                .map(songMapper::toResponseModel);
    }

    @Override
    public Flux<PlaylistResponse> findAllLiked(final Long userId) {
        return playlistService.findAllLiked(userId)
                .flatMap(like -> Mono.zip(
                                        playlistService.findById(like.id()),
                                        songService.findAllSongIdsByPlaylistId(like.id()).collect(Collectors.toSet())
                                )
                                .map(tuple -> {
                                    final var playlist = tuple.getT1();
                                    final var songIds = tuple.getT2();
                                    return playlistMapper.toDomainModel(playlist, like, songIds);
                                })
                                .map(playlistMapper::toResponseModel)
                );
    }

    @Override
    public Mono<PlaylistResponse> save(final PlaylistRequest request, final FilePart image) {
        return playlistService.save(playlistMapper.toDataModel(request))
                .map(playlistMapper::toDomainModel)
                .flatMap(playlist -> {
                    if (image != null) {
                        return fileService.updatePlaylist(playlist, image)
                                .thenReturn(playlist);
                    }
                    return Mono.just(playlist);
                })
                .map(playlistMapper::toResponseModel);
    }

    @Override
    public Mono<LikeResponse> toggleLike(final Long userId, final Long playlistId) {
        return playlistService.toggleLike(userId, playlistId)
                .map(like -> new LikeResponse(playlistId, like));
    }

    @Override
    public Mono<PlaylistSongResponse> toggleSong(final UserSecurity userSecurity, final Long playlistId, final Long songId) {
        return playlistService.findById(playlistId)
                .flatMap(playlist -> {
                    userAllowedToExecute(playlist.userId(), userSecurity, "Playlist can only be changed by its owner");
                    return playlistService.toggleSong(playlist.id(), songId);
                })
                .map(saved -> new PlaylistSongResponse(playlistId, songId, saved));
    }

    @Override
    public Mono<PlaylistResponse> update(final UserSecurity userSecurity, final PlaylistRequest request) {
        return playlistService.findById(request.id())
                .flatMap(playlist -> {
                    userAllowedToExecute(playlist.userId(), userSecurity, "User cannot update playlist that is not his own");
                    return Mono.zip(
                            playlistService.update(playlist, playlistMapper.toDataModel(request)),
                            playlistService.findLikeById(userSecurity.id(), playlist.id()),
                            songService.findAllSongIdsByPlaylistId(playlist.id()).collect(Collectors.toSet())
                    );
                })
                .map(tuple -> {
                    final var playlist = tuple.getT1();
                    final var like = tuple.getT2();
                    final var songIds = tuple.getT3();
                    return playlistMapper.toDomainModel(playlist, like, songIds);
                })
                .map(playlistMapper::toResponseModel);
    }

    @Override
    public Mono<Void> delete(final UserSecurity userSecurity, final Long playlistId) {
        return playlistService.findById(playlistId)
                .flatMap(playlist -> {
                    userAllowedToExecute(playlist.userId(), userSecurity, "User cannot delete playlist that is not his own");
                    return playlistService.delete(playlist);
                });
    }
}
