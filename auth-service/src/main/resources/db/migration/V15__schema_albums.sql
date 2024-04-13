CREATE TABLE albums
(
    id             BIGSERIAL    UNIQUE NOT NULL,
    user_id        BIGINT              NOT NULL REFERENCES users (id),
    name           VARCHAR(255)        NOT NULL,
    description    VARCHAR(255),
    image_filename VARCHAR(255),
    created_at     TIMESTAMPTZ         NOT NULL,
    updated_at     TIMESTAMPTZ         NOT NULL,
    PRIMARY KEY (id)
);

CREATE INDEX albums_user_id_key ON albums (user_id);
CREATE INDEX albums_name_key ON albums (name);
