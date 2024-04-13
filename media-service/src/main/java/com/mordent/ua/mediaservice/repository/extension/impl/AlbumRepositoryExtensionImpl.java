package com.mordent.ua.mediaservice.repository.extension.impl;

import com.mordent.ua.mediaservice.model.data.Like;
import com.mordent.ua.mediaservice.repository.extension.AlbumRepositoryExtension;
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
public class AlbumRepositoryExtensionImpl implements AlbumRepositoryExtension {

    private static final String QUERY_FIND_LIKE_ALBUMS_BY_USER_ID = "SELECT la.album_id AS la_album_id, la.timestamp AS la_timestamp FROM like_albums AS la WHERE la.user_id=:userId";
    private static final String QUERY_FIND_ALBUM_LIKE_BY_USER_ID_AND_PLAYLIST_ID = "SELECT la.album_id AS la_album_id, la.timestamp AS la_timestamp FROM like_albums AS la WHERE la.user_id=:userId AND la.album_id=:albumId";
    private static final String QUERY_GET_COUNT_ALBUM_LIKES = "SELECT COUNT(*) AS album_likes FROM like_albums AS la WHERE la.album_id=:albumId";
    private static final String QUERY_SAVE_ALBUM_LIKE = "INSERT INTO like_albums (user_id, album_id, timestamp) VALUES (:userId, :albumId, :timestamp)";
    private static final String QUERY_DELETE_ALBUM_LIKES_BY_ALBUM_ID = "DELETE FROM like_albums AS la WHERE la.album_id=:albumId";
    private static final String QUERY_DELETE_ALBUM_LIKE_BY_USER_ID_AND_ALBUM_ID = "DELETE FROM like_albums AS la WHERE la.user_id=:userId AND la.album_id=:albumId";

    private final DatabaseClient databaseClient;

    @Override
    public Flux<Like> findAllLikeAlbumsByUserId(final Long userId) {
        return databaseClient.sql(QUERY_FIND_LIKE_ALBUMS_BY_USER_ID)
                .bind("userId", userId)
                .fetch()
                .all()
                .map(this::mapToSongLike);
    }

    @Override
    public Mono<Like> findAlbumLike(final Long userId, final Long albumId) {
        return databaseClient.sql(QUERY_FIND_ALBUM_LIKE_BY_USER_ID_AND_PLAYLIST_ID)
                .bind("userId", userId)
                .bind("albumId", albumId)
                .fetch()
                .one()
                .map(this::mapToSongLike)
                .switchIfEmpty(Mono.just(new Like(albumId, false, null)));
    }

    @Override
    public Mono<Long> getCountAlbumLikes(final Long albumId) {
        return databaseClient.sql(QUERY_GET_COUNT_ALBUM_LIKES)
                .bind("albumId", albumId)
                .fetch()
                .one()
                .map(row -> (Long) row.getOrDefault("album_likes", 0L))
                .switchIfEmpty(Mono.just(0L));
    }

    @Override
    public Mono<Boolean> saveAlbumLike(final Long userId, final Long albumId) {
        return databaseClient.sql(QUERY_SAVE_ALBUM_LIKE)
                .bind("userId", userId)
                .bind("albumId", albumId)
                .bind("timestamp", Instant.now().atZone(ZoneOffset.UTC).toOffsetDateTime())
                .fetch()
                .rowsUpdated()
                .map(rows -> rows == 1);
    }

    @Override
    public Mono<Void> deleteAlbumLikes(final Long albumId) {
        return databaseClient.sql(QUERY_DELETE_ALBUM_LIKES_BY_ALBUM_ID)
                .bind("albumId", albumId)
                .fetch()
                .rowsUpdated()
                .then();
    }

    @Override
    public Mono<Boolean> deleteAlbumLike(final Long userId, final Long albumId) {
        return databaseClient.sql(QUERY_DELETE_ALBUM_LIKE_BY_USER_ID_AND_ALBUM_ID)
                .bind("userId", userId)
                .bind("albumId", albumId)
                .fetch()
                .rowsUpdated()
                .map(rows -> rows == 1);
    }

    private Like mapToSongLike(final Map<String, Object> row) {
        if (row.isEmpty()) return new Like(null, false, null);
        return new Like(
                (Long) getOrThrow(row.get("la_album_id")),
                true,
                ((OffsetDateTime) getOrThrow(row.get("la_timestamp"))).toInstant()
        );
    }
}
