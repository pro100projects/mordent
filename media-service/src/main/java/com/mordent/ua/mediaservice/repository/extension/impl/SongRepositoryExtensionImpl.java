package com.mordent.ua.mediaservice.repository.extension.impl;

import com.mordent.ua.mediaservice.model.data.Like;
import com.mordent.ua.mediaservice.repository.extension.SongRepositoryExtension;
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
public class SongRepositoryExtensionImpl implements SongRepositoryExtension {

    private static final String QUERY_FIND_LIKE_SONGS_BY_USER_ID = "SELECT ls.song_id AS ls_song_id, ls.timestamp AS ls_timestamp FROM like_songs AS ls WHERE ls.user_id=:userId";
    private static final String QUERY_FIND_SONG_LIKE_BY_USER_ID_AND_SONG_ID = "SELECT ls.song_id AS ls_song_id, ls.timestamp AS ls_timestamp FROM like_songs AS ls WHERE ls.user_id=:userId AND ls.song_id=:songId";
    private static final String QUERY_GET_SONG_LIKES = "SELECT COUNT(*) AS song_likes FROM like_songs AS ls WHERE ls.song_id = :songId";
    private static final String QUERY_GET_SONGS_LIKES_IN_ALBUM = "SELECT COUNT(*) AS total_songs_likes FROM like_songs AS ls INNER JOIN songs AS s ON s.id = ls.song_id WHERE s.album_id = :albumId";
    private static final String QUERY_SAVE_SONG_LIKE = "INSERT INTO like_songs (user_id, song_id, timestamp) VALUES (:userId, :songId, :timestamp)";
    private static final String QUERY_DELETE_SONG_LIKES = "DELETE FROM like_songs AS ls WHERE ls.song_id=:songId";
    private static final String QUERY_DELETE_SONG_LIKE = "DELETE FROM like_songs AS ls WHERE ls.user_id=:userId AND ls.song_id=:songId";

    private final DatabaseClient databaseClient;

    @Override
    public Flux<Like> findAllLikeSongsByUserId(final Long userId) {
        return databaseClient.sql(QUERY_FIND_LIKE_SONGS_BY_USER_ID)
                .bind("userId", userId)
                .fetch()
                .all()
                .map(this::mapToSongLike);
    }

    @Override
    public Mono<Like> findSongLike(final Long userId, final Long songId) {
        return databaseClient.sql(QUERY_FIND_SONG_LIKE_BY_USER_ID_AND_SONG_ID)
                .bind("userId", userId)
                .bind("songId", songId)
                .fetch()
                .one()
                .map(this::mapToSongLike)
                .switchIfEmpty(Mono.just(new Like(songId, false, null)));
    }

    @Override
    public Mono<Long> getCountSongLikes(final Long songId) {
        return databaseClient.sql(QUERY_GET_SONG_LIKES)
                .bind("songId", songId)
                .fetch()
                .one()
                .map(row -> (Long) row.getOrDefault("song_likes", 0L))
                .switchIfEmpty(Mono.just(0L));
    }

    @Override
    public Mono<Long> getCountSongsLikesInAlbum(final Long albumId) {
        return databaseClient.sql(QUERY_GET_SONGS_LIKES_IN_ALBUM)
                .bind("albumId", albumId)
                .fetch()
                .one()
                .map(row -> (Long) row.getOrDefault("total_songs_likes", 0L))
                .switchIfEmpty(Mono.just(0L));
    }

    @Override
    public Mono<Boolean> saveSongLike(final Long userId, final Long songId) {
        return databaseClient.sql(QUERY_SAVE_SONG_LIKE)
                .bind("userId", userId)
                .bind("songId", songId)
                .bind("timestamp", Instant.now().atZone(ZoneOffset.UTC).toOffsetDateTime())
                .fetch()
                .rowsUpdated()
                .map(rows -> rows == 1);
    }

    @Override
    public Mono<Void> deleteSongLikes(final Long songId) {
        return databaseClient.sql(QUERY_DELETE_SONG_LIKES)
                .bind("songId", songId)
                .fetch()
                .rowsUpdated()
                .then();
    }

    @Override
    public Mono<Boolean> deleteSongLike(final Long userId, final Long songId) {
        return databaseClient.sql(QUERY_DELETE_SONG_LIKE)
                .bind("userId", userId)
                .bind("songId", songId)
                .fetch()
                .rowsUpdated()
                .map(rows -> rows == 1);
    }

    private Like mapToSongLike(final Map<String, Object> row) {
        if (row.isEmpty()) return new Like(null, false, null);
        return new Like(
                (Long) getOrThrow(row.get("ls_song_id")),
                true,
                ((OffsetDateTime) getOrThrow(row.get("ls_timestamp"))).toInstant()
        );
    }
}
