package com.mordent.ua.mediaservice.service.impl;

import com.mordent.ua.mediaservice.model.data.Like;
import com.mordent.ua.mediaservice.model.data.Playlist;
import com.mordent.ua.mediaservice.model.domain.ErrorCode;
import com.mordent.ua.mediaservice.model.exception.MediaException;
import com.mordent.ua.mediaservice.repository.PlaylistRepository;
import com.mordent.ua.mediaservice.service.PlaylistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PlaylistServiceImpl implements PlaylistService {

    private final PlaylistRepository playlistRepository;

    @Override
    public Flux<Playlist> findAll(final String name) {
        if (name == null || name.trim().isEmpty()) {
            return playlistRepository.findAll();
        } else {
            return playlistRepository.findAllByNameIsContainingIgnoreCase(name.trim());
        }
    }

    @Override
    public Mono<Playlist> findById(final Long playlistId) {
        return playlistRepository.findById(playlistId)
                .switchIfEmpty(Mono.error(new MediaException(ErrorCode.PLAYLIST_NOT_FOUND, "Playlist is not exist")));
    }

    @Override
    public Mono<Long> getCountPlaylistLikes(final Long playlistId) {
        return playlistRepository.getCountPlaylistLikes(playlistId);
    }

    @Override
    public Flux<Playlist> findAllByUserId(final Long userId) {
        return playlistRepository.findAllByUserId(userId);
    }

    @Override
    public Flux<Like> findAllLiked(final Long userId) {
        return playlistRepository.findAllLikePlaylistsByUserId(userId);
    }

    @Override
    public Mono<Like> findLikeById(final Long userId, final Long playlistId) {
        return playlistRepository.findPlaylistLike(userId, playlistId);
    }

    @Override
    public Mono<Playlist> save(final Playlist playlist) {
        return playlistRepository.save(playlist);
    }

    @Override
    public Mono<Boolean> toggleLike(final Long userId, final Long playlistId) {
        return findById(playlistId)
                .flatMap(playlist -> playlistRepository.findPlaylistLike(userId, playlist.id())
                        .flatMap(songLike -> songLike.liked() ?
                                playlistRepository.deletePlaylistLike(userId, playlist.id()).map(flag -> !flag)
                                :
                                playlistRepository.savePlaylistLike(userId, playlist.id())
                        )
                );
    }

    @Override
    public Mono<Boolean> toggleSong(final Long playlistId, final Long songId) {
        return playlistRepository.findPlaylistSong(playlistId, songId)
                .flatMap(playlistSong -> playlistSong.songId() == null ?
                        playlistRepository.savePlaylistSong(playlistSong.playlistId(), songId)
                        :
                        playlistRepository.deletePlaylistSong(playlistSong.playlistId(), playlistSong.songId()).map(flag -> !flag)
                );
    }

    @Override
    public Mono<Playlist> update(final Playlist oldPlaylist, final Playlist newPlaylist) {
        return playlistRepository.save(oldPlaylist.overwritingVariables(newPlaylist));
    }

    @Override
    public Mono<Void> delete(final Playlist playlist) {
        return playlistRepository.deletePlaylistSongs(playlist.id())
                .then(playlistRepository.deletePlaylistLikes(playlist.id()))
                .then(playlistRepository.delete(playlist))
                .onErrorResume(error -> Mono.error(new MediaException(ErrorCode.UNEXPECTED, error.getMessage())));
    }
}
