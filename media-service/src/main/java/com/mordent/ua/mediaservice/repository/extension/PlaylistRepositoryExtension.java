package com.mordent.ua.mediaservice.repository.extension;

import com.mordent.ua.mediaservice.model.data.Like;
import com.mordent.ua.mediaservice.model.data.PlaylistSong;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PlaylistRepositoryExtension {

    //region liked playlists

    Flux<Like> findAllLikePlaylistsByUserId(Long userId);

    Mono<Like> findPlaylistLike(Long userId, Long playlistId);

    Mono<Long> getCountPlaylistLikes(Long playlistId);

    Mono<Boolean> savePlaylistLike(Long userId, Long playlistId);

    Mono<Void> deletePlaylistLikes(Long playlistId);

    Mono<Boolean> deletePlaylistLike(Long userId, Long playlistId);

    //endregion

    //region playlist songs

    Flux<PlaylistSong> findAllPlaylistSongsByPlaylistId(Long playlistId);

    Mono<PlaylistSong> findPlaylistSong(Long playlistId, Long songId);

    Mono<Boolean> savePlaylistSong(Long playlistId, Long songId);

    Mono<Void> deletePlaylistSongs(Long playlistId);

    Mono<Void> deletePlaylistSong(Long songId);

    Mono<Boolean> deletePlaylistSong(Long playlistId, Long songId);

    //endregion
}
