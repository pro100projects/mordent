CREATE TABLE playlists
(
    id             BIGSERIAL    UNIQUE NOT NULL,
    user_id        BIGINT              NOT NULL REFERENCES users (id),
    name           VARCHAR(255)        NOT NULL,
    description    VARCHAR(255),
    image_filename VARCHAR(255),
    is_private     BOOLEAN             NOT NULL,
    created_at     TIMESTAMPTZ         NOT NULL,
    updated_at     TIMESTAMPTZ         NOT NULL,
    PRIMARY KEY (id)
);

CREATE INDEX playlists_user_id_key ON playlists (user_id);
CREATE INDEX playlists_name_key ON playlists (name);
