CREATE TABLE users (
  id serial PRIMARY KEY,
  name varchar(25),
  email varchar(50),
  role char(1)
);

CREATE UNIQUE INDEX uk_user_email ON users (email);


insert into users(name, email, role) values ('Gandalf', 'gandalf@whitewizard.com', 'A');
insert into users(name, email, role) values ('Frodo Baggins', 'frodo.baggins@theshire.com', 'A');
insert into users(name, email, role) values ('Samwise Gamgee', 'samgamgee@theshire.com', 'C');
insert into users(name, email, role) values ('Fangorn', 'fangorn@email.com', 'C');