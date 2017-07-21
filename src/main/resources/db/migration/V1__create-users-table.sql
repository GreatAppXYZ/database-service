CREATE TABLE IF NOT EXISTS users(
  user_id SERIAL PRIMARY KEY,
  name                  TEXT,
  email                 TEXT UNIQUE,
  password              TEXT NOT NULL,
  rol                   TEXT NOT NULL,
  must_change_password  BOOLEAN NOT NULL DEFAULT TRUE,
  active                BOOLEAN NOT NULL DEFAULT FALSE,
  activation_key        TEXT UNIQUE
);

ALTER TABLE users ADD CONSTRAINT users_name_len CHECK (length(name) < 200);
ALTER TABLE users ADD CONSTRAINT users_email_len CHECK (length(email) < 200);
