DELETE from short_urls;
DELETE from users;

-- Test data for authentication tests
-- BCrypt hash for 'password123' with strength 10
-- Password: 'differentpassword' for different@example.com
INSERT INTO users (id, email, password, name, role, created_at, updated_at) VALUES
(1,'user@example.com', '$2a$10$1zzle7heXsK7DYn4P.gG5OMr.RnSLI9E2KMPwZ6iqlSJLO0W75pje', 'Test User', 'ROLE_USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'auth@example.com', '$2a$10$1zzle7heXsK7DYn4P.gG5OMr.RnSLI9E2KMPwZ6iqlSJLO0W75pje', 'Auth User', 'ROLE_USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'unique@example.com', '$2a$10$1zzle7heXsK7DYn4P.gG5OMr.RnSLI9E2KMPwZ6iqlSJLO0W75pje', 'First User', 'ROLE_USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'different@example.com', '$2a$10$Gzhi26wFooDB3cElb.TyeOEr30YJDygRtFvN0k9PC.PRM7tQIQvE2', 'Second User', 'ROLE_USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Test data for short URLs
INSERT INTO short_urls (short_key, original_url, is_private, created_by_id, click_count, created_at, updated_at, expires_at) VALUES
('abc123', 'https://example.com/long/path', false, 1, 42, '2025-08-01 10:15:30', '2025-08-01 10:15:30', '2025-12-31 00:00:00'),
('def456', 'https://google.com', false, 2, 25, '2025-08-02 14:20:15', '2025-08-02 14:20:15', null),
('ghi789', 'https://private-url.com', true, 1, 5, '2025-08-03 09:10:45', '2025-08-03 09:10:45', null),
('jkl012', 'https://spring.io', false, null, 100, '2025-08-04 16:30:20', '2025-08-04 16:30:20', '2026-01-01 00:00:00'),
('mno345', 'https://github.com', false, 2, 0, '2025-08-05 11:45:30', '2025-08-05 11:45:30', null),
('pqr678', 'https://stackoverflow.com', false, 1, 75, '2025-08-06 08:15:10', '2025-08-06 08:15:10', null),
('stu901', 'https://private-site.com', true, 2, 3, '2025-08-07 13:25:40', '2025-08-07 13:25:40', '2025-09-01 00:00:00'),
('vwx234', 'https://baeldung.com', false, null, 15, '2025-08-08 19:40:55', '2025-08-08 19:40:55', null),
('yza567', 'https://news.ycombinator.com', false, 3, 12, '2025-08-09 07:10:00', '2025-08-09 07:10:00', null),
('bcd890', 'https://reddit.com/r/java', true, 1, 8, '2025-08-10 12:30:00', '2025-08-10 12:30:00', '2025-10-01 00:00:00'),
('cde123', 'https://twitter.com', false, null, 54, '2025-08-11 09:45:00', '2025-08-11 09:45:00', null),
('efg456', 'https://linkedin.com', false, 2, 33, '2025-08-12 15:20:00', '2025-08-12 15:20:00', null),
('hij789', 'https://medium.com', true, 4, 2, '2025-08-13 11:05:00', '2025-08-13 11:05:00', '2025-09-15 00:00:00'),
('klm012', 'https://docker.com', false, 1, 77, '2025-08-14 18:40:00', '2025-08-14 18:40:00', null),
('nop345', 'https://kubernetes.io', false, 3, 19, '2025-08-15 10:10:00', '2025-08-15 10:10:00', null),
('qrs678', 'https://spring.academy', true, 2, 4, '2025-08-16 13:55:00', '2025-08-16 13:55:00', null),
('tuv901', 'https://baidu.com', false, null, 1, '2025-08-17 21:25:00', '2025-08-17 21:25:00', null),
('wxy234', 'https://bing.com', false, 4, 6, '2025-08-18 06:35:00', '2025-08-18 06:35:00', '2026-01-15 00:00:00'),
('zaa567', 'https://apache.org', false, 2, 13, '2025-08-19 14:45:00', '2025-08-19 14:45:00', null),
('abb890', 'https://maven.apache.org', true, 1, 5, '2025-08-20 09:15:00', '2025-08-20 09:15:00', null),
('acc123', 'https://gradle.org', false, 3, 22, '2025-08-21 17:50:00', '2025-08-21 17:50:00', null),
('add456', 'https://github.blog', false, 4, 0, '2025-08-22 08:05:00', '2025-08-22 08:05:00', null),
('aee789', 'https://openjdk.java.net', true, 2, 3, '2025-08-23 12:25:00', '2025-08-23 12:25:00', '2025-12-31 00:00:00'),
('aff012', 'https://jetbrains.com', false, null, 41, '2025-08-24 19:30:00', '2025-08-24 19:30:00', null),
('agg345', 'https://oracle.com/java', false, 1, 9, '2025-08-25 11:40:00', '2025-08-25 11:40:00', null),
('ahh678', 'https://stackoverflow.blog', true, 3, 7, '2025-08-26 16:55:00', '2025-08-26 16:55:00', null),
('aii901', 'https://dzone.com', false, 4, 18, '2025-08-27 07:20:00', '2025-08-27 07:20:00', null),
('ajj234', 'https://dev.to', false, 2, 29, '2025-08-28 20:10:00', '2025-08-28 20:10:00', '2026-02-01 00:00:00')
;