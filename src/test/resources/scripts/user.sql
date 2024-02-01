insert into users(name, email, password, role) values ('Gandalf', 'gandalf@whitewizard.com', '$2a$12$hmKqxOM4Ef7IlBHszwT7Xu0SVthCosHM9CzXFfnRu2SFngXoZRnxK', 'A');
insert into users(name, email, password, role) values ('Frodo Baggins', 'frodo.baggins@theshire.com', '$2a$12$hmKqxOM4Ef7IlBHszwT7Xu0SVthCosHM9CzXFfnRu2SFngXoZRnxK', 'A');
insert into users(name, email, password, role) values ('Samwise Gamgee', 'samgamgee@theshire.com', '$2a$12$hmKqxOM4Ef7IlBHszwT7Xu0SVthCosHM9CzXFfnRu2SFngXoZRnxK', 'C');
insert into users(name, email, password, role) values ('Fangorn', 'fangorn@email.com', '$2a$12$hmKqxOM4Ef7IlBHszwT7Xu0SVthCosHM9CzXFfnRu2SFngXoZRnxK', 'C');
insert into users(name, email, password, role) values ('Elrond', 'elrond@email.com', '$2a$12$hmKqxOM4Ef7IlBHszwT7Xu0SVthCosHM9CzXFfnRu2SFngXoZRnxK', 'A');

insert into sessions(token, updated_at, expiration_at, email)
values ('428034dd06a4465ba1d4995338b90e85', now(), now() + INTERVAL '1 day', 'gandalf@whitewizard.com');

insert into sessions(token, updated_at, expiration_at, email)
values ('544034dd06a4465cb2e5005338b90e85', now(), now() + INTERVAL '1 day', 'fangorn@email.com');

insert into sessions(token, updated_at, expiration_at, email)
values ('539145ee06a4465ba1d4995338b12345', now() + INTERVAL '-1 day', now() + INTERVAL '-2 day', 'fangorn@email.com');