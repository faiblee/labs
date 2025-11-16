CREATE TABLE IF NOT EXISTS functions (
	id SERIAL PRIMARY KEY,
	name varchar(100) NOT NULL,
	owner_id INT NOT NULL,
	type varchar(32) NOT NULL, --tabulated, constant, composite, sqr, identity
	FOREIGN KEY (owner_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_functions_owner_id ON functions USING HASH (owner_id); --индекс для поиска по owner_id
