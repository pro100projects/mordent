CREATE TABLE like_playlists
(
    user_id BIGINT NOT NULL REFERENCES users (id),
    playlist_id BIGINT NOT NULL REFERENCES playlists (id),
    timestamp TIMESTAMPTZ NOT NULL
);

CREATE INDEX like_playlists_user_id_key ON like_playlists (user_id);
CREATE INDEX like_playlists_playlist_id_key ON like_playlists (playlist_id);
