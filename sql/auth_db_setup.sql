-- Create auth_db if missing
CREATE DATABASE IF NOT EXISTS auth_db;
USE auth_db;

-- Create users_auth table
CREATE TABLE IF NOT EXISTS users_auth (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL
);

-- Insert admin login credentials
-- Password is: admin123 (bcrypt hashed)
INSERT INTO users_auth (username, password, role)
VALUES (
    'admin1',
    '$2a$10$FaSU3D0Rh7w./KjcXrYlgPLueOetFdn5uBF.HA3wmh1qyKnWF8b2K', 
    'admin'
);
