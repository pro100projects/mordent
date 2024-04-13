CREATE TABLE user_roles
(
    user_id BIGINT      NOT NULL REFERENCES users (id),
    role    VARCHAR(10) NOT NULL
);

CREATE INDEX user_roles_user_id_key ON user_roles (user_id);
