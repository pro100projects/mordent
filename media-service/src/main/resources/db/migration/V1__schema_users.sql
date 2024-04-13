CREATE TABLE users
(
    id         BIGSERIAL    UNIQUE NOT NULL,
    name       VARCHAR(255)        NOT NULL,
    surname    VARCHAR(255)        NOT NULL,
    username   VARCHAR(255) UNIQUE NOT NULL,
    email      VARCHAR(255) UNIQUE NOT NULL,
    password   VARCHAR(255)        NOT NULL,
    created_at TIMESTAMP           NOT NULL,
    updated_at TIMESTAMP           NOT NULL,
    enabled    BOOLEAN             NOT NULL,
    uuid       UUID         UNIQUE,
    PRIMARY KEY (id)
);

CREATE INDEX users_name_key ON users (name);
CREATE INDEX users_surname_key ON users (surname);
