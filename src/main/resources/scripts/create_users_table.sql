create table users (
    id serial primary key,
    username varchar(100) not null,
    password_hash varchar(255) not null
);