-- Online Reservation System — database schema and seed data
-- Run with: mysql -u root -p < database.sql
-- (The app also creates/updates this schema automatically on startup.)

CREATE DATABASE IF NOT EXISTS train_reservation;
USE train_reservation;

CREATE TABLE IF NOT EXISTS users (
  id            INT AUTO_INCREMENT PRIMARY KEY,
  username      VARCHAR(50)  NOT NULL UNIQUE,
  password_hash CHAR(64)     NOT NULL,
  full_name     VARCHAR(100) NOT NULL,
  created_at    TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS trains (
  train_number VARCHAR(10)  PRIMARY KEY,
  train_name   VARCHAR(120) NOT NULL,
  from_station VARCHAR(100),
  to_station   VARCHAR(100)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS reservations (
  id                  INT AUTO_INCREMENT PRIMARY KEY,
  pnr                 VARCHAR(10)  NOT NULL UNIQUE,
  passenger_name      VARCHAR(100) NOT NULL,
  train_number        VARCHAR(10)  NOT NULL,
  train_name          VARCHAR(120) NOT NULL,
  class_type          VARCHAR(40)  NOT NULL,
  journey_date        DATE         NOT NULL,
  source_station      VARCHAR(100) NOT NULL,
  destination_station VARCHAR(100) NOT NULL,
  booked_by           VARCHAR(50)  NOT NULL,
  booking_time        TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- Demo users (passwords are stored as SHA-256 hashes):
--   admin / admin123
--   user  / user123
INSERT IGNORE INTO users (username, password_hash, full_name) VALUES
  ('admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'Administrator'),
  ('user',  'e606e38b0d8c19b24cf0ee3808183162ea7cd63ff7912dbb22b5e803286b4446', 'Demo User');

INSERT IGNORE INTO trains (train_number, train_name, from_station, to_station) VALUES
  ('12301', 'Howrah Rajdhani Express',     'New Delhi',                 'Howrah'),
  ('12951', 'Mumbai Rajdhani Express',     'New Delhi',                 'Mumbai Central'),
  ('12621', 'Tamil Nadu Express',          'New Delhi',                 'Chennai Central'),
  ('12627', 'Karnataka Express',           'New Delhi',                 'Bengaluru City'),
  ('12723', 'Telangana Express',           'Hyderabad',                 'New Delhi'),
  ('12903', 'Golden Temple Mail',          'Mumbai CST',                'Amritsar'),
  ('12009', 'Shatabdi Express',            'New Delhi',                 'Bandra Terminus'),
  ('12423', 'Dibrugarh Rajdhani Express',  'New Delhi',                 'Dibrugarh'),
  ('12133', 'Punjab Mail',                 'Mumbai CST',                'New Delhi'),
  ('12625', 'Kerala Express',              'New Delhi',                 'Thiruvananthapuram Central');
