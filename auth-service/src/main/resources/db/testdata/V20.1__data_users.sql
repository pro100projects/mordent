INSERT INTO users VALUES
                    (1, 'Bogdan', 'Tkachuk', 'pro100user', 'pro100user@gmail.com', '$2a$10$TufvuJ5IfBtbF4JsuRjgZO/mRoBG4lTzTtLx9hFp0tSfmspKX6hT.', '2023-01-19 13:40:57.675068', '2023-01-19 13:40:57.675663', true, null, 'MORDENT', null, 'avatar'),
                    (2, 'Bogdan', 'Tkachuk', 'bogdan.tkachuk@gmail.com', 'bogdan.tkachuk@gmail.com', 'password', NOW(), NOW(), false, null, 'GOOGLE_NOT_ACTIVATE', 'token', 'avatar');

ALTER SEQUENCE users_id_seq RESTART WITH 3;