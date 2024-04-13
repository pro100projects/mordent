package com.mordent.ua.mediaservice.service.impl;

import com.mordent.ua.mediaservice.model.data.*;
import com.mordent.ua.mediaservice.model.domain.ErrorCode;
import com.mordent.ua.mediaservice.model.domain.SongStatistic;
import com.mordent.ua.mediaservice.model.domain.SongWithMetadata;
import com.mordent.ua.mediaservice.model.exception.MediaException;
import com.mordent.ua.mediaservice.repository.*;
import com.mordent.ua.mediaservice.service.SongService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.time.Instant;
import java.util.Comparator;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class SongServiceImpl implements SongService {

    private final SongRepository songRepository;
    private final SongMetadataRepository songMetadataRepository;
    private final UserRepository userRepository;
    private final AlbumRepository albumRepository;
    private final PlaylistRepository playlistRepository;

    @Override
    public Flux<Song> findAll(final String name) {
        if (name == null || name.trim().isEmpty()) {
            return songRepository.findAll();
        } else {
            return songRepository.findAllByNameIsContainingIgnoreCase(name.trim());
        }
    }

    @Override
    public Mono<Song> findById(final Long songId) {
        return songRepository.findById(songId)
                .switchIfEmpty(Mono.error(new MediaException(ErrorCode.SONG_NOT_FOUND, "Song is not exist")));
    }

    @Override
    public Mono<SongMetadata> findSongMetadataById(final Long songId) {
        return songMetadataRepository.findById(songId);
    }

    @Override
    public Mono<Like> findSongLike(final Long userId, final Long songId) {
        return songRepository.findSongLike(userId, songId);
    }

    @Override
    public Mono<SongStatistic> getStatisticById(final Long songId) {
        return findById(songId)
                .flatMap(song -> songRepository.getCountSongLikes(songId))
                .map(songLikes -> new SongStatistic(songId, songLikes));
    }

    @Override
    public Flux<Tuple2<Like, Song>> findAllLiked(final Long userId) {
        return songRepository.findAllLikeSongsByUserId(userId)
                .flatMap(likeSong -> songRepository.findById(likeSong.id())
                        .map(song -> Tuples.of(likeSong, song))
                );
    }

    @Override
    public Mono<Song> save(final Song song) {
        final var newsong = song.toBuilder()
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        return songRepository.save(newsong);
    }

    @Override
    public Mono<Song> listen(final Long songId) {
        return findById(songId)
                .map(song -> song.toBuilder().playback(song.playback() + 1).build())
                .flatMap(songRepository::save);
    }

    @Override
    public Mono<Boolean> toggleLike(final Long userId, final Long songId) {
        return findById(songId)
                .flatMap(song -> songRepository.findSongLike(userId, song.id())
                        .flatMap(songLike -> songLike.liked() ?
                                songRepository.deleteSongLike(userId, song.id()).map(flag -> !flag)
                                :
                                songRepository.saveSongLike(userId, song.id())
                        )
                );
    }

    @Override
    public Mono<Song> update(final Song oldSong, final Song newSong) {
        if (newSong.albumId() == null) return songRepository.save(oldSong.overwritingVariables(newSong));
        return albumRepository.existsByAlbumIdAndUserId(newSong.albumId(), oldSong.userId())
                .flatMap(value -> Boolean.TRUE.equals(value) ?
                        songRepository.save(oldSong.overwritingVariables(newSong))
                        :
                        Mono.error(new MediaException(ErrorCode.NOT_ALLOWED, "User cannot set album that is not his own"))
                );
    }

    @Override
    public Mono<Void> delete(final Song song) {
        return playlistRepository.deletePlaylistSong(song.id())
                .then(songRepository.deleteSongLikes(song.id()))
                .then(songRepository.delete(song))
                .onErrorResume(error -> Mono.error(new MediaException(ErrorCode.UNEXPECTED, error.getMessage())));
    }

    @Override
    public Flux<SongWithMetadata> findAllByAlbumId(final Long userId, final Long albumId) {
        return songRepository.findAllByAlbumId(albumId)
                .flatMap(song -> mapToSongWithMetadata(userId, song));
    }

    @Override
    public Flux<Long> findAllSongIdsByAlbumId(final Long albumId) {
        return songRepository.findAllByAlbumId(albumId).map(Song::id);
    }

    @Override
    public Flux<SongWithMetadata> findAllByPlaylistId(final Long userId, final Long playlistId) {
        return playlistRepository.findAllPlaylistSongsByPlaylistId(playlistId)
                .sort(Comparator.comparing(PlaylistSong::timestamp))
                .flatMap(playlistSong -> songRepository.findById(playlistSong.songId()))
                .flatMap(song -> mapToSongWithMetadata(userId, song));
    }

    @Override
    public Flux<Long> findAllSongIdsByPlaylistId(final Long playlistId) {
        return playlistRepository.findAllPlaylistSongsByPlaylistId(playlistId).map(PlaylistSong::songId);
    }

    private Mono<SongWithMetadata> mapToSongWithMetadata(final Long userId, final Song song) {
        return Mono.zip(
                        userRepository.findById(song.userId()),
                        song.albumId() != null ? albumRepository.findById(song.albumId()) : Mono.just(new Album(null, null, null, null, null, null, null)),
                        songRepository.findSongLike(userId, song.id()),
                        songMetadataRepository.findById(song.id())
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
