CREATE TABLE IF NOT EXISTS points (
	id BIGSERIAL PRIMARY KEY,
	x_value REAL NOT NULL,
	y_value REAL NOT NULL,
	function_id INT NOT NULL,
	FOREIGN KEY (function_id) REFERENCES functions(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_points_function_id ON points USING HASH (function_id); --индекс для поиска по function_id