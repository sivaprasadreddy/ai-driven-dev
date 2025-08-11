DELETE from users;
-- Test data for authentication tests
-- BCrypt hash for 'password123' with strength 10
INSERT INTO users (email, password, name, role, created_at, updated_at) VALUES
('user@example.com', '$2a$10$1zzle7heXsK7DYn4P.gG5OMr.RnSLI9E2KMPwZ6iqlSJLO0W75pje', 'Test User', 'ROLE_USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('auth@example.com', '$2a$10$1zzle7heXsK7DYn4P.gG5OMr.RnSLI9E2KMPwZ6iqlSJLO0W75pje', 'Auth User', 'ROLE_USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('unique@example.com', '$2a$10$1zzle7heXsK7DYn4P.gG5OMr.RnSLI9E2KMPwZ6iqlSJLO0W75pje', 'First User', 'ROLE_USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('different@example.com', '$2a$10$Gzhi26wFooDB3cElb.TyeOEr30YJDygRtFvN0k9PC.PRM7tQIQvE2', 'Second User', 'ROLE_USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Password: 'password123' for user@example.com and auth@example.com
-- Password: 'differentpassword' for different@example.com