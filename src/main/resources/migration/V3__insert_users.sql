-- Insert initial users (admin and standard user)
-- BCrypt hash here is for the raw password: password
INSERT INTO users (username, password)
VALUES
    ('admin', '$2a$10$2b1cY.F4hYFh8kP9c1xReO3zZcQKpP3nqvK6f7iZx1u9Jj0bqgT3m'),
    ('user',  '$2a$10$2b1cY.F4hYFh8kP9c1xReO3zZcQKpP3nqvK6f7iZx1u9Jj0bqgT3m')
ON CONFLICT (username) DO NOTHING;