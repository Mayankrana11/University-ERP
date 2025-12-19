USE auth_db;
DELETE FROM users_auth;

-- Password for everyone is: 'password' (since we cannot hash 'student123' on the fly)
-- Hash: $2a$10$FaSU3D0Rh7w./KjcXrYlgPlueOetFdn5uBF.HA3wmh1qyKnWF8b2K
INSERT INTO users_auth (username, password, role) VALUES 
('admin1',      '$2a$10$FaSU3D0Rh7w./KjcXrYlgPlueOetFdn5uBF.HA3wmh1qyKnWF8b2K', 'admin'),
('inst1',       '$2a$10$FaSU3D0Rh7w./KjcXrYlgPlueOetFdn5uBF.HA3wmh1qyKnWF8b2K', 'instructor'),
('MAYANK RANA', '$2a$10$FaSU3D0Rh7w./KjcXrYlgPlueOetFdn5uBF.HA3wmh1qyKnWF8b2K', 'instructor'),
('student1',    '$2a$10$FaSU3D0Rh7w./KjcXrYlgPlueOetFdn5uBF.HA3wmh1qyKnWF8b2K', 'student'),
('student2',    '$2a$10$FaSU3D0Rh7w./KjcXrYlgPlueOetFdn5uBF.HA3wmh1qyKnWF8b2K', 'student'),
('student3',    '$2a$10$FaSU3D0Rh7w./KjcXrYlgPlueOetFdn5uBF.HA3wmh1qyKnWF8b2K', 'student');

USE erp_db;
-- Clear old courses to avoid duplicates
DELETE FROM courses;
INSERT INTO courses (course_name, instructor_name, credits, capacity, drop_deadline) VALUES 
('Data Structures',   'inst1',       4, 30, '2025-11-17'),
('Algorithms',        'inst1',       3, 30, '2025-12-01'),
('Database Systems',  'inst1',       4, 30, '2025-12-15'),
('Discrete Maths',    'MAYANK RANA', 4, 30, '2025-11-30');