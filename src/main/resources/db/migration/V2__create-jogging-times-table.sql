CREATE TABLE IF NOT EXISTS jogging_times(
  _id SERIAL PRIMARY KEY,
  jogging_times_id TEXT UNIQUE NOT NULL,
  user_id          TEXT NOT NULL,
  day              INTEGER,
  month            INTEGER,
  year             INTEGER,
  distance         INTEGER,
  time             INTEGER
);

