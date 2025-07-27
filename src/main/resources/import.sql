
INSERT INTO users (id, email, name, surname) VALUES (52, 'pino@gmail.com', 'pino', 'pino');
INSERT INTO credentials (id, user_id, password, role, username) VALUES (52, 52, '$2a$10$m4HgUj4vu7dHtTvhLzQk4u3lKqBqhYeaPcNztDnDZ3VXnWYeHBzpK', 'USER', 'pino');

INSERT INTO users (id, email, name, surname) VALUES (1, 'admin@gmail.com', 'ammi', 'ammi');
INSERT INTO credentials (id, user_id, password, role, username) VALUES (1, 1, '$2a$10$guFnH6lIKxhz8Pgp4P4Vs.1sBc848xHtgv8KjjvHvCshicv4OOqpq', 'ADMIN', 'ammi');
