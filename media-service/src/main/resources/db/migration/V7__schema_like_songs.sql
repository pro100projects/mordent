CREATE TABLE like_songs
(
    user_id BIGINT NOT NULL REFERENCES users (id),
    song_id BIGINT NOT NULL REFERENCES songs (id)
);

CREATE INDEX like_songs_user_id_key ON like_songs (user_id);
CREATE INDEX like_songs_song_id_key ON like_songs (song_id);
