CREATE TABLE users (
  id serial PRIMARY KEY,
  name varchar(25),
  email varchar(50),
  password varchar(500),
  role char(1)
);

CREATE TABLE authorization_log (
    id serial PRIMARY KEY,
    decision_id varchar(40),
    path varchar(50),
    actions varchar(30),
    email varchar(50),
    result char(1)
);

CREATE UNIQUE INDEX uk_user_email ON users (email);