package com.mordent.ua.mediaservice.repository.extension.impl;

import com.mordent.ua.mediaservice.model.data.Like;
import com.mordent.ua.mediaservice.model.data.PlaylistSong;
import com.mordent.ua.mediaservice.repository.extension.PlaylistRepositoryExtension;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;

import static com.mordent.ua.mediaservice.utils.Nulls.getOrThrow;

@Slf4j
@RequiredArgsConstructor
public class PlaylistRepositoryExtensionImpl implements PlaylistRepositoryExtension {

    private static final String QUERY_FIND_LIKE_PLAYLISTS_BY_USER_ID = "SELECT lp.playlist_id AS lp_playlist_id, lp.timestamp AS lp_timestamp FROM like_playlists AS lp WHERE lp.user_id=:userId";
    private static final String QUERY_FIND_PLAYLIST_LIKE_BY_USER_ID_AND_PLAYLIST_ID = "SELECT lp.playlist_id AS lp_playlist_id, lp.timestamp AS lp_timestamp FROM like_playlists AS lp WHERE lp.user_id=:userId AND lp.playlist_id=:playlistId";
    private static final String QUERY_GET_COUNT_PLAYLIST_LIKES = "SELECT COUNT(*) AS playlist_likes FROM like_playlists AS lp WHERE lp.playlist_id=:playlistId";
    private static final String QUERY_SAVE_PLAYLIST_LIKE = "INSERT INTO like_playlists (user_id, playlist_id, timestamp) VALUES (:userId, :playlistId, :timestamp)";
    private static final String QUERY_DELETE_PLAYLIST_LIKES_BY_PLAYLIST_ID = "DELETE FROM like_playlists AS lp WHERE lp.playlist_id=:playlistId";
    private static final String QUERY_DELETE_PLAYLIST_LIKE_BY_USER_ID_AND_PLAYLIST_ID = "DELETE FROM like_playlists AS lp WHERE lp.user_id=:userId AND lp.playlist_id=:playlistId";

    private static final String QUERY_FIND_PLAYLIST_SONGS_BY_PLAYLIST_ID = "SELECT ps.playlist_id AS ps_playlist_id, ps.song_id AS ps_song_id, ps.timestamp AS ps_timestamp FROM playlist_songs AS ps WHERE ps.playlist_id=:playlistId";
    private static final String QUERY_FIND_PLAYLIST_SONG_BY_PLAYLIST_ID_AND_SONG_ID = "SELECT ps.playlist_id AS ps_playlist_id, ps.song_id AS ps_song_id, ps.timestamp AS ps_timestamp FROM playlist_songs AS ps WHERE ps.playlist_id=:playlistId AND ps.song_id=:songId";
    private static final String QUERY_SAVE_PLAYLIST_SONG = "INSERT INTO playlist_songs (playlist_id, song_id, timestamp) VALUES (:playlistId, :songId, :timestamp)";
    private static final String QUERY_DELETE_PLAYLIST_SONGS_BY_PLAYLIST_ID = "DELETE FROM playlist_songs AS ps WHERE ps.playlist_id=:playlistId";
    private static final String QUERY_DELETE_PLAYLIST_SONGS_BY_SONG_ID = "DELETE FROM playlist_songs AS ps WHERE ps.song_id=:songId";
    private static final String QUERY_DELETE_PLAYLIST_SONG_BY_PLAYLIST_ID_AND_SONG_ID = "DELETE FROM playlist_songs AS ps WHERE ps.playlist_id=:playlistId AND ps.song_id=:songId";

    private final DatabaseClient databaseClient;

    //region liked playlists

    @Override
    public Flux<Like> findAllLikePlaylistsByUserId(final Long userId) {
        return databaseClient.sql(QUERY_FIND_LIKE_PLAYLISTS_BY_USER_ID)
                .bind("userId", userId)
                .fetch()
                .all()
                .map(this::mapToSongLike);
    }

    @Override
    public Mono<Like> findPlaylistLike(final Long userId, final Long playlistId) {
        return databaseClient.sql(QUERY_FIND_PLAYLIST_LIKE_BY_USER_ID_AND_PLAYLIST_ID)
                .bind("userId", userId)
                .bind("playlistId", playlistId)
                .fetch()
                .one()
                .map(this::mapToSongLike)
                .switchIfEmpty(Mono.just(new Like(playlistId, false, null)));
    }

    @Override
    public Mono<Long> getCountPlaylistLikes(final Long playlistId) {
        return databaseClient.sql(QUERY_GET_COUNT_PLAYLIST_LIKES)
                .bind("playlistId", playlistId)
                .fetch()
                .one()
                .map(row -> (Long) row.getOrDefault("playlist_likes", 0L))
                .switchIfEmpty(Mono.just(0L));
    }

    @Override
    public Mono<Boolean> savePlaylistLike(final Long userId, final Long playlistId) {
        return databaseClient.sql(QUERY_SAVE_PLAYLIST_LIKE)
                .bind("userId", userId)
                .bind("playlistId", playlistId)
                .bind("timestamp", Instant.now().atZone(ZoneOffset.UTC).toOffsetDateTime())
                .fetch()
                .rowsUpdated()
                .map(rows -> rows == 1);
    }

    @Override
    public Mono<Void> deletePlaylistLikes(final Long playlistId) {
        return databaseClient.sql(QUERY_DELETE_PLAYLIST_LIKES_BY_PLAYLIST_ID)
                .bind("playlistId", playlistId)
                .fetch()
                .rowsUpdated()
                .then();
    }

    @Override
    public Mono<Boolean> deletePlaylistLike(final Long userId, final Long playlistId) {
        return databaseClient.sql(QUERY_DELETE_PLAYLIST_LIKE_BY_USER_ID_AND_PLAYLIST_ID)
                .bind("userId", userId)
                .bind("playlistId", playlistId)
                .fetch()
                .rowsUpdated()
                .map(rows -> rows == 1);
    }

    //endregion

    //region playlist songs

    @Override
    public Flux<PlaylistSong> findAllPlaylistSongsByPlaylistId(final Long playlistId) {
        return databaseClient.sql(QUERY_FIND_PLAYLIST_SONGS_BY_PLAYLIST_ID)
                .bind("playlistId", playlistId)
                .fetch()
                .all()
                .map(this::mapToPlaylistSong);
    }

    @Override
    public Mono<PlaylistSong> findPlaylistSong(final Long playlistId, final Long songId) {
        return databaseClient.sql(QUERY_FIND_PLAYLIST_SONG_BY_PLAYLIST_ID_AND_SONG_ID)
                .bind("playlistId", playlistId)
                .bind("songId", songId)
                .fetch()
                .one()
                .map(this::mapToPlaylistSong)
                .switchIfEmpty(Mono.just(new PlaylistSong(playlistId, null, null)));
    }

    @Override
    public Mono<Boolean> savePlaylistSong(final Long playlistId, final Long songId) {
        return databaseClient.sql(QUERY_SAVE_PLAYLIST_SONG)
                .bind("playlistId", playlistId)
                .bind("songId", songId)
                .bind("timestamp", Instant.now().atZone(ZoneOffset.UTC).toOffsetDateTime())
                .fetch()
                .rowsUpdated()
                .map(rows -> rows == 1);
    }

    @Override
    public Mono<Void> deletePlaylistSongs(final Long playlistId) {
        return databaseClient.sql(QUERY_DELETE_PLAYLIST_SONGS_BY_PLAYLIST_ID)
                .bind("playlistId", playlistId)
                .fetch()
                .rowsUpdated()
                .then();
    }

    @Override
    public Mono<Void> deletePlaylistSong(final Long songId) {
        return databaseClient.sql(QUERY_DELETE_PLAYLIST_SONGS_BY_SONG_ID)
                .bind("songId", songId)
                .fetch()
                .rowsUpdated()
                .then();
    }

    @Override
    public Mono<Boolean> deletePlaylistSong(final Long playlistId, final Long songId) {
        return databaseClient.sql(QUERY_DELETE_PLAYLIST_SONG_BY_PLAYLIST_ID_AND_SONG_ID)
                .bind("playlistId", playlistId)
                .bind("songId", songId)
                .fetch()
                .rowsUpdated()
                .map(rows -> rows == 1);
    }

    //endregion

    private Like mapToSongLike(final Map<String, Object> row) {
        if (row.isEmpty()) return new Like(null, false, null);
        return new Like(
                (Long) getOrThrow(row.get("lp_playlist_id")),
                true,
                ((OffsetDateTime) getOrThrow(row.get("lp_timestamp"))).toInstant()
        );
    }

    private PlaylistSong mapToPlaylistSong(final Map<String, Object> row) {
        if (row.isEmpty()) return new PlaylistSong(null, null, null);
        return new PlaylistSong(
                (Long) getOrThrow(row.get("ps_playlist_id")),
                (Long) getOrThrow(row.get("ps_song_id")),
                ((OffsetDateTime) getOrThrow(row.get("ps_timestamp"))).toInstant()
        );
    }
}
