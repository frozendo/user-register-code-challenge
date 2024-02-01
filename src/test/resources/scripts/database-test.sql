CREATE TABLE users (
  id serial PRIMARY KEY,
  name varchar(25),
  email varchar(50),
  password varchar(500),
  role char(1)
);

CREATE TABLE sessions (
    id serial PRIMARY KEY,
    token varchar(32),
    updated_at timestamp,
    expiration_at timestamp,
    email varchar(50)
);

CREATE UNIQUE INDEX uk_user_email ON users (email);