-- ========================================================
-- Campus Complaint Tracker Database Schema (MySQL)
-- ========================================================

CREATE DATABASE IF NOT EXISTS `complaint_tracker_db`;
USE `complaint_tracker_db`;

-- Drop existing tables if needed
DROP TABLE IF EXISTS `complaints`;
DROP TABLE IF EXISTS `users`;

-- 1. Users Table
CREATE TABLE `users` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `full_name` VARCHAR(100) NOT NULL,
  `email` VARCHAR(150) NOT NULL UNIQUE,
  `password` VARCHAR(255) NOT NULL,
  `role` VARCHAR(20) NOT NULL DEFAULT 'USER',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. Complaints Table
CREATE TABLE `complaints` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(200) NOT NULL,
  `description` TEXT NOT NULL,
  `category` VARCHAR(30) NOT NULL,
  `building` VARCHAR(100) NOT NULL,
  `room_number` VARCHAR(20) DEFAULT NULL,
  `priority` VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
  `status` VARCHAR(20) NOT NULL DEFAULT 'OPEN',
  `image_url` VARCHAR(500) DEFAULT NULL,
  `remarks` TEXT DEFAULT NULL,
  `assigned_to` VARCHAR(100) DEFAULT NULL,
  `reported_by` BIGINT NOT NULL,
  `created_by` VARCHAR(150) DEFAULT NULL,
  `updated_by` VARCHAR(150) DEFAULT NULL,
  `deleted` TINYINT(1) NOT NULL DEFAULT 0,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_complaints_user` FOREIGN KEY (`reported_by`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
