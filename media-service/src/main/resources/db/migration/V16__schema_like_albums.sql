CREATE TABLE like_albums
(
    user_id BIGINT NOT NULL REFERENCES users (id),
    album_id BIGINT NOT NULL REFERENCES albums (id),
    timestamp TIMESTAMPTZ NOT NULL
);

CREATE INDEX like_albums_user_id_key ON like_albums (user_id);
CREATE INDEX like_albums_album_id_key ON like_albums (album_id);
