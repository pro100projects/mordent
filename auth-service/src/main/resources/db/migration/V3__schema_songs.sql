CREATE TABLE songs
(
    id         BIGSERIAL    UNIQUE NOT NULL,
    user_id    BIGINT              NOT NULL REFERENCES users (id),
    name       VARCHAR(255)        NOT NULL,
    text       VARCHAR(255),
    photo      VARCHAR(255),
    filename   VARCHAR(255)        NOT NULL,
    playback   BIGINT              NOT NULL,
    created_at TIMESTAMP           NOT NULL,
    updated_at TIMESTAMP           NOT NULL,
    PRIMARY KEY (id)
);

CREATE INDEX songs_user_id_key ON songs (user_id);
CREATE INDEX songs_name_key ON songs (name);
