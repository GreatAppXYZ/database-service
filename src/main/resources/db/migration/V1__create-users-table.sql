CREATE TABLE IF NOT EXISTS users(
  _id SERIAL PRIMARY KEY,
  user_id               TEXT UNIQUE NOT NULL,
  name                  TEXT,
  email                 TEXT UNIQUE,
  password              TEXT NOT NULL,
  role                  TEXT NOT NULL,
  must_change_password  BOOLEAN NOT NULL DEFAULT FALSE,
  active                BOOLEAN NOT NULL DEFAULT FALSE,
  activation_key        TEXT UNIQUE
);

ALTER TABLE users ADD CONSTRAINT users_name_len CHECK (length(name) < 200);
ALTER TABLE users ADD CONSTRAINT users_email_len CHECK (length(email) < 200);
