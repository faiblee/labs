CREATE TABLE IF NOT EXISTS users (
	id SERIAL PRIMARY KEY,
	username varchar(100) UNIQUE NOT NULL,
	password_hash varchar(255) NOT NULL,
	factory_type varchar(16) NOT NULL DEFAULT ('array'), --array/linkedList
	role varchar(8) NOT NULL DEFAULT ('user') --user/admin
);