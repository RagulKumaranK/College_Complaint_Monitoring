-- ========================================================
-- Sample Seed Data for Campus Complaint Tracker
-- Passwords are BCrypt encoded (raw: admin123, student123, faculty123)
-- ========================================================

INSERT INTO `users` (`id`, `full_name`, `email`, `password`, `role`, `created_at`) VALUES
(1, 'Admin User', 'admin@campus.edu', '$2a$10$EblZqNptyYvcLm/VwDCVAu.z1aU/.n5J4u1ZqS6A0yCjH1JgZqQWe', 'ADMIN', NOW()),
(2, 'John Doe', 'john.doe@student.campus.edu', '$2a$10$EblZqNptyYvcLm/VwDCVAu.z1aU/.n5J4u1ZqS6A0yCjH1JgZqQWe', 'USER', NOW()),
(3, 'Dr. Sarah Smith', 'sarah.smith@faculty.campus.edu', '$2a$10$EblZqNptyYvcLm/VwDCVAu.z1aU/.n5J4u1ZqS6A0yCjH1JgZqQWe', 'USER', NOW());

INSERT INTO `complaints` (`id`, `title`, `description`, `category`, `building`, `room_number`, `priority`, `status`, `remarks`, `assigned_to`, `reported_by`, `deleted`, `created_at`, `updated_at`) VALUES
(1, 'Ceiling Fan Noise and Low Speed', 'The ceiling fan in Room 302 is making loud screeching noises and running very slow.', 'FAN', 'Academic Block A', 'Room 302', 'MEDIUM', 'OPEN', NULL, NULL, 2, 0, NOW(), NOW()),
(2, 'Projector Display Flickering', 'HDMI display port on the projector flickers constantly during lectures.', 'PROJECTOR', 'Science Building', 'Auditorium 1', 'HIGH', 'IN_PROGRESS', 'Technician assigned, replacement cable requested.', 'Tech Support Team', 3, 0, NOW(), NOW()),
(3, 'Water Leakage in Restroom', 'Severe water leakage from pipe under wash basin causing floor flooding.', 'WATER_LEAKAGE', 'Library Complex', '2nd Floor Restroom', 'CRITICAL', 'ASSIGNED', NULL, 'Plumbing Dept', 2, 0, NOW(), NOW()),
(4, 'Damaged Wooden Bench', 'Bench near entrance has broken wooden slates, potential injury hazard.', 'FURNITURE', 'Student Activity Center', 'Main Lobby', 'LOW', 'RESOLVED', 'Bench replaced with new furniture.', NULL, 2, 0, NOW(), NOW());
