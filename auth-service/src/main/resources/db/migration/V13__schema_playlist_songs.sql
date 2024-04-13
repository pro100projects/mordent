CREATE TABLE playlist_songs
(
    playlist_id BIGINT NOT NULL REFERENCES playlists (id),
    song_id BIGINT NOT NULL REFERENCES songs (id),
    timestamp TIMESTAMPTZ NOT NULL
);

CREATE INDEX playlist_songs_playlist_id_key ON playlist_songs (playlist_id);
CREATE INDEX playlist_songs_song_id_key ON playlist_songs (song_id);
