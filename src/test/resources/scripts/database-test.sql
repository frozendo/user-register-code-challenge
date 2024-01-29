CREATE TABLE users (
  id serial PRIMARY KEY,
  name varchar(25),
  email varchar(50),
  role char(1)
);

CREATE UNIQUE INDEX uk_user_email ON users (email);