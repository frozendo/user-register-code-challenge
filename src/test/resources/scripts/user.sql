insert into users(name, email, role) values ('Gandalf', 'gandalf@whitewizard.com', 'A');
insert into users(name, email, role) values ('Frodo Baggins', 'frodo.baggins@theshire.com', 'A');
insert into users(name, email, role) values ('Samwise Gamgee', 'samgamgee@theshire.com', 'C');
insert into users(name, email, role) values ('Fangorn', 'fangorn@email.com', 'C');

insert into sessions(token, updated_at, expiration_at, email)
values ('428034dd06a4465ba1d4995338b90e85', now(), now() + INTERVAL '1 day', 'gandalf@whitewizard.com');

insert into sessions(token, updated_at, expiration_at, email)
values ('544034dd06a4465cb2e5005338b90e85', now(), now() + INTERVAL '1 day', 'fangorn@email.com');

insert into sessions(token, updated_at, expiration_at, email)
values ('539145ee06a4465ba1d4995338b12345', now() + INTERVAL '-1 day', now() + INTERVAL '-2 day', 'fangorn@email.com');